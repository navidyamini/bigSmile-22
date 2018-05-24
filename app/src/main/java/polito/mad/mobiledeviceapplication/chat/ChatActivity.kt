package polito.mad.mobiledeviceapplication.chat

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.Calendar

import polito.mad.mobiledeviceapplication.utils.AppConstants
import polito.mad.mobiledeviceapplication.utils.Chat
import polito.mad.mobiledeviceapplication.utils.Constants

/**
 * Created by user on 24/05/2018.
 */

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        val i = intent
        val chat = Chat("Sergio", "Navid", FirebaseAuth.getInstance().currentUser!!.uid, i.getStringExtra(AppConstants.USER_ID_R), "Ciao", Calendar.getInstance().timeInMillis)
        sendMessageToFirebaseUser(applicationContext, chat, "")


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    fun sendMessageToFirebaseUser(context: Context,
                                  chat: Chat,
                                  receiverFirebaseToken: String) {
        val room_type_1 = chat.senderUid + "_" + chat.receiverUid
        val room_type_2 = chat.receiverUid + "_" + chat.senderUid

        val databaseReference = FirebaseDatabase.getInstance()
                .reference

        databaseReference.child(Constants.ARG_CHAT_ROOMS)
                .ref
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e("EEE", "sendMessageToFirebaseUser: $room_type_1 exists")
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(chat.timestamp.toString())
                                    .setValue(chat)
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e("EEE", "sendMessageToFirebaseUser: $room_type_2 exists")
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_2)
                                    .child(chat.timestamp.toString())
                                    .setValue(chat)
                        } else {
                            Log.e("EEE", "sendMessageToFirebaseUser: success")
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(chat.timestamp.toString())
                                    .setValue(chat)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Unable to send message.
                    }
                })
    }

    fun getMessageFromFirebaseUser(senderUid: String, receiverUid: String) {
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid

        val databaseReference = FirebaseDatabase.getInstance()
                .reference

        databaseReference.child(Constants.ARG_CHAT_ROOMS)
                .ref
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e("EEE", "getMessageFromFirebaseUser: $room_type_1 exists")
                            FirebaseDatabase.getInstance()
                                    .reference
                                    .child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .addChildEventListener(object : ChildEventListener {
                                        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
                                            // Chat message is retreived.
                                            val chat = dataSnapshot.getValue(Chat::class.java)
                                        }

                                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                                        }

                                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                        }

                                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Unable to get message.
                                        }
                                    })
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e("EEE", "getMessageFromFirebaseUser: $room_type_2 exists")
                            FirebaseDatabase.getInstance()
                                    .reference
                                    .child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_2)
                                    .addChildEventListener(object : ChildEventListener {
                                        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String) {
                                            // Chat message is retreived.
                                            val chat = dataSnapshot.getValue(Chat::class.java)
                                        }

                                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                                        }

                                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                        }

                                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Unable to get message.
                                        }
                                    })
                        } else {
                            Log.e("EEE", "getMessageFromFirebaseUser: no such room available")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Unable to get message
                    }
                })
    }
}
