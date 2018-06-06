package polito.mad.mobiledeviceapplication.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import polito.mad.mobiledeviceapplication.MainActivity
import polito.mad.mobiledeviceapplication.R
import polito.mad.mobiledeviceapplication.utils.Chat
import polito.mad.mobiledeviceapplication.utils.Constants

/**
 * Created by user on 25/05/2018.
 */

class ChatService : android.app.Service() {

    private var sender: String? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        FirebaseAuth.getInstance().currentUser?.uid?.let { getMessageFromAllFirebaseUsers(it) }


        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun getMessageFromAllFirebaseUsers(receiverUid: String) {


        val databaseReference = FirebaseDatabase.getInstance()
                .reference

        databaseReference.child(Constants.ARG_CHAT_ROOMS).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (chats in dataSnapshot.children) {

                    chats.ref.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                            for (child in dataSnapshot.children) {

                                if (child.key == "sender")
                                    sender = child.value as String?


                            }


                            var bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_email_white_24dp)
                            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)

                            val builder = NotificationCompat.Builder(applicationContext, "Message")
                                    .setContentTitle(sender)
                                    .setContentText("A new message arrived!")
                                    .setStyle(NotificationCompat.BigTextStyle().bigText("A new messages arrived!"))
                                    .setSmallIcon(R.drawable.ic_email_white_24dp)
                                    .setAutoCancel(true)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setVibrate(longArrayOf(0, 250, 250, 250))

                            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            notificationManager.notify(30, builder.build())
                            println("NOTIFICATION")

                        }

                        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                        }

                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                        }

                        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {

                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })


                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }


}
