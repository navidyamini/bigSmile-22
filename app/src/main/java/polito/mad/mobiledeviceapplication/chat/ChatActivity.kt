package polito.mad.mobiledeviceapplication.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import polito.mad.mobiledeviceapplication.R
import polito.mad.mobiledeviceapplication.utils.AppConstants
import polito.mad.mobiledeviceapplication.utils.Chat
import polito.mad.mobiledeviceapplication.utils.Constants

/**
 * Created by user on 24/05/2018.
 */

class ChatActivity : AppCompatActivity() {

    private var chat_list: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var chats: ArrayList<Chat>? = null
    private var mChatAdapter: ChatAdapter? = null
    private var mFormat: SimpleDateFormat? = null
    private var input: EditText? = null
    private var send: ImageButton? = null
    private var send_image: ImageButton? = null
    private var myToolbar: Toolbar? = null
    private var i: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        myToolbar = findViewById<View>(R.id.toolbar_chat) as Toolbar
        setSupportActionBar(myToolbar)

        supportActionBar!!.setTitle(intent.getStringExtra(AppConstants.USER_USERNAME_R))


        i = intent


        input = findViewById<View>(R.id.send_message) as EditText
        send = findViewById<View>(R.id.send) as ImageButton
        send_image = findViewById<View>(R.id.send_image) as ImageButton

        send!!.setOnClickListener {
            if (input!!.text.toString() != "") {

                val chat = Chat(i!!.getStringExtra(AppConstants.USER_USERNAME_S), i!!.getStringExtra(AppConstants.USER_USERNAME_R), FirebaseAuth.getInstance().currentUser!!.uid, i!!.getStringExtra(AppConstants.USER_ID_R), input!!.text.toString(),"NEW" ,Calendar.getInstance().timeInMillis)
                sendMessageToFirebaseUser(applicationContext, chat, "")
                input!!.setText("")

            }
        }

        send_image!!.setOnClickListener { }

        chat_list = findViewById<View>(R.id.chat_list) as RecyclerView

        mLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        chat_list!!.layoutManager = mLayoutManager
        chats = ArrayList()

        mChatAdapter = ChatAdapter(applicationContext, chats!!)

        chat_list!!.adapter = mChatAdapter


        getMessageFromFirebaseUser(FirebaseAuth.getInstance().currentUser!!.uid, i!!.getStringExtra(AppConstants.USER_ID_R))

        mFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

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

                            getMessageFromFirebaseUser(FirebaseAuth.getInstance().currentUser!!.uid, i!!.getStringExtra(AppConstants.USER_ID_R))

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
                                        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                            // Chat message is retreived.
                                            val chat = dataSnapshot.getValue(Chat::class.java)
                                            if (chat != null) {
                                                chats?.add(chat)
                                                if (receiverUid != FirebaseAuth.getInstance().currentUser!!.uid) {
                                                    val map = HashMap<String, Any>()
                                                    map.set("flag", "OLD")
                                                    //chat.toMap().set("flag", "OLD")
                                                    System.out.println(map)
                                                    for (chat in dataSnapshot.children) {

                                                        //for (message in chat.children) {

                                                            //for (field in message.children) {
                                                                if (chat.key == "flag") {
                                                                    FirebaseDatabase.getInstance()
                                                                            .reference
                                                                            .child(Constants.ARG_CHAT_ROOMS)
                                                                            .child(room_type_2)
                                                                            .updateChildren(map)

                                                                }


                                                           // }

                                                        //}
                                                    }

                                                }
                                            }

                                            mChatAdapter?.notifyDataSetChanged()
                                            chat_list?.scrollToPosition(mChatAdapter!!.mDataset.size - 1)

                                        }

                                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                                        }

                                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                        }

                                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

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
                                        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                            // Chat message is retreived.
                                            val chat = dataSnapshot.getValue(Chat::class.java)
                                            chats!!.add(chat!!)
                                            if (receiverUid != FirebaseAuth.getInstance().currentUser!!.uid ) {
                                                val map = HashMap<String,Any>()
                                                map.set("flag","OLD")
                                                //chat.toMap().set("flag", "OLD")

                                                System.out.println(map)
                                                for (chat in dataSnapshot.children) {

                                                    //for (message in chat.children) {

                                                        //for (field in message.children) {
                                                            if(chat.key =="flag"){
                                                                FirebaseDatabase.getInstance()
                                                                        .reference
                                                                        .child(Constants.ARG_CHAT_ROOMS)
                                                                        .child(room_type_2)
                                                                        .updateChildren(map)

                                                            }


                                                        //}

                                                   // }
                                                }

                                            }
                                            mChatAdapter!!.notifyDataSetChanged()
                                            chat_list!!.scrollToPosition(mChatAdapter!!.mDataset.size - 1)

                                        }

                                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                                        }

                                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                        }

                                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

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


    internal inner class ChatAdapter(private val context: Context, public val mDataset: ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        inner class ViewHolder(var mView: View, var message: TextView, var timestamp: TextView) : RecyclerView.ViewHolder(mView)

        override fun getItemViewType(position: Int): Int {

            return if (mDataset[position].senderUid == FirebaseAuth.getInstance().currentUser!!.uid)
                0
            else
                1

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {

            when (viewType) {

                0 -> {

                    val v1 = LayoutInflater.from(parent.context).inflate(R.layout.item_my_message, parent, false)
                    val message = v1.findViewById<TextView>(R.id.message)
                    val timestamp = v1.findViewById<TextView>(R.id.timestamp)


                    return ViewHolder(v1, message, timestamp)
                }

                1 -> {

                    val v2 = LayoutInflater.from(parent.context).inflate(R.layout.item_other_message, parent, false)
                    val message2 = v2.findViewById<TextView>(R.id.message)
                    val timestamp2 = v2.findViewById<TextView>(R.id.timestamp)


                    return ViewHolder(v2, message2, timestamp2)
                }
            }

            return null!!
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.timestamp.text = mFormat!!.format(Date(mDataset[position].timestamp))
            holder.message.text = mDataset[position].message


        }


        override fun getItemCount(): Int {
            return mDataset.size
        }


    }


}
