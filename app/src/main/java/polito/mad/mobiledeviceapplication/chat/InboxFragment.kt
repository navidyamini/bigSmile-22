package polito.mad.mobiledeviceapplication.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_chat_list.*

import java.util.ArrayList

import polito.mad.mobiledeviceapplication.MainActivity
import polito.mad.mobiledeviceapplication.R
import polito.mad.mobiledeviceapplication.utils.Constants

/**
 * Created by Asus on 5/24/2018.
 */

class InboxFragment : Fragment() {

    private val myDatabase: DatabaseReference? = null
    private val name_inbox: RelativeLayout? = null
    private var chat_list: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var userid: String? = null
    private var chat_user_ids: ArrayList<String>? = null
    private var chat_user_names: ArrayList<String>? = null
    private var inboxAdapter: InboxAdapter? = null


    interface FragChatObserver {
        fun notifyChatRequest(intent: Intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_inbox, container, false) as ViewGroup

        (activity as MainActivity).toolbar.setTitle(R.string.inbox)

        chat_list = rootView.findViewById<View>(R.id.chat_list) as RecyclerView
        chat_user_ids = ArrayList()
        chat_user_names = ArrayList()

        mLayoutManager = LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        chat_list!!.layoutManager = mLayoutManager

        inboxAdapter = InboxAdapter(context!!, chat_user_names!!)
        val intent = activity!!.intent

        chat_list!!.adapter = inboxAdapter

        userid = FirebaseAuth.getInstance().currentUser!!.uid


        val databaseReference = FirebaseDatabase.getInstance().reference.child(Constants.ARG_CHAT_ROOMS)

        databaseReference.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var receiverUid = "null"
                        var senderUid = "null"
                        var receiverName = "null"
                        var senderName = "null"
                        var key = "null"
                        if (dataSnapshot.hasChildren()) {
                            for (chat in dataSnapshot.children) {

                                for (message in chat.children) {

                                    for (field in message.children) {
                                        key = field.key

                                        if (key == "receiverUid") {
                                            receiverUid = field.value as String
                                        }
                                        if (key == "senderUid") {
                                            senderUid = field.value as String
                                        }
                                        if (key == "receiver") {
                                            receiverName = field.value as String
                                        }
                                        if (key == "sender") {
                                            senderName = field.value as String
                                        }
                                    }

                                }
                                if (userid == receiverUid) {
                                    chat_user_ids!!.add(senderUid)
                                    chat_user_names!!.add(senderName)
                                }
                                if (userid == senderUid) {
                                    chat_user_ids!!.add(receiverUid)
                                    chat_user_names!!.add(receiverName)
                                }
                            }
                        }

                        inboxAdapter!!.notifyDataSetChanged()

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //handle databaseError
                    }
                })

        return rootView
    }

    internal inner class InboxAdapter(private val context: Context, private val mDataset: ArrayList<String>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

        inner class ViewHolder(var mView: View, var userName: TextView, val unreadMessage:ImageView ) : RecyclerView.ViewHolder(mView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val unreadMessage: ImageView?= null
            val v1 = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
            //TextView ID = v1.findViewById(R.id.ID);
            val name = v1.findViewById<TextView>(R.id.name_inbox)

            val unreadMessage = v1.findViewById<ImageView>(R.id.unreadMessage)
            val databaseReference = FirebaseDatabase.getInstance().reference.child(Constants.ARG_CHAT_ROOMS)
/*
            databaseReference.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var key = "null"
                            var receiverUid = "null"
                            var sender = "null"
                            var flag="null"
                            if (dataSnapshot.hasChildren()) {
                                for (chat in dataSnapshot.children) {

                                    for (message in chat.children) {

                                        for (field in message.children) {
                                            key = field.key

                                            if (key == "flag") {
                                                flag= field.value as String
                                            }
                                            if(key == "sender"){
                                                sender = field.value as String
                                            }
                                            if (key == "receiverUid") {
                                                receiverUid = field.value as String

                                                if(flag=="old"){
                                                    receiverUid="null"
                                                    sender = "null"
                                                }
                                            }
                                            if(flag == "new" && receiverUid==userid && sender!="null"){
                                                unreadMessage.visibility = View.VISIBLE
                                            }

                                        }

                                    }

                                }
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //handle databaseError
                        }
                    })*/

            return ViewHolder(v1, name, unreadMessage)

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.userName.text = mDataset[position]
            val databaseReference = FirebaseDatabase.getInstance().reference.child(Constants.ARG_CHAT_ROOMS)
            databaseReference.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var key = "null"
                            var receiverUid = "null"
                            var sender = "null"
                            var flag="null"
                            if (dataSnapshot.hasChildren()) {
                                for (chat in dataSnapshot.children) {

                                    for (message in chat.children) {

                                        for (field in message.children) {
                                            key = field.key

                                            if (key == "flag") {
                                                flag= field.value as String
                                            }
                                            if(key == "sender"){
                                                sender = field.value as String
                                            }
                                            if (key == "receiverUid") {
                                                receiverUid = field.value as String

                                                if(flag=="old"){
                                                    receiverUid="null"
                                                    sender = "null"
                                                }
                                            }
                                            if(flag == "new" && receiverUid==userid && sender== holder.userName.text){
                                                holder.unreadMessage.visibility = View.VISIBLE
                                            }

                                        }

                                    }

                                }
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //handle databaseError
                        }
                    })
            //holder.userName.text = mDataset[position]
            holder.mView.setOnClickListener {
                val a = activity
                if (a is InboxFragment.FragChatObserver) {
                    val observer = a as InboxFragment.FragChatObserver?
                    val intent = Intent(Constants.CHAT_REQUEST)
                    intent.putExtra("user_id_r", chat_user_ids!![position])
                    intent.putExtra("username_r", chat_user_names!![position])
                    observer!!.notifyChatRequest(intent)
                    holder.unreadMessage.visibility = View.INVISIBLE
/*
                    val databaseReference = FirebaseDatabase.getInstance().reference.child(Constants.ARG_CHAT_ROOMS)
                    databaseReference.addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var key = "null"
                                    var receiverUid = "null"
                                    var flag="null"
                                    if (dataSnapshot.hasChildren()) {
                                        for (chat in dataSnapshot.children) {

                                            for (message in chat.children) {

                                                for (field in message.children) {
                                                    key = field.key

                                                    if (key == "flag") {
                                                        flag= field.value as String
/*
                                                        if(field.value == "new") {
                                                            field.ref.setValue("old")
                                                        }*/
                                                    }

                                                    if (key == "receiverUid") {
                                                        receiverUid = field.value as String

                                                        if(flag=="old"){
                                                            receiverUid="null"
                                                        }
                                                    }
                                                    if(flag == "new" && receiverUid==userid){
                                                        unreadMessage.visibility = View.INVISIBLE
                                                        for (field in message.children) {
                                                            key = field.key

                                                            if (key == "flag") {
                                                                flag= field.value as String

                                                            if(field.value == "new") {
                                                                field.ref.setValue("old")
                                                            }
                                                            }
                                                        }

                                                    }

                                                }

                                            }

                                        }
                                    }

                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    //handle databaseError
                                }
                            })*/


                }
            }
        }


        override fun getItemCount(): Int {
            return mDataset.size
        }


    }


}

