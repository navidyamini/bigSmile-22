package polito.mad.mobiledeviceapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.AppConstants;
import polito.mad.mobiledeviceapplication.utils.Chat;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 24/05/2018.
 */

public class ChatActivityJava extends AppCompatActivity {

    private RecyclerView chat_list;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Chat> chats;
    private ChatAdapter mChatAdapter;
    private SimpleDateFormat mFormat;
    private EditText input;
    private ImageButton send,send_image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final Intent i = getIntent();



        input = (EditText) findViewById(R.id.send_message);
        send = (ImageButton) findViewById(R.id.send);
        send_image = (ImageButton) findViewById(R.id.send_image);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!input.getText().toString().equals("")) {

                    Chat chat = new Chat(i.getStringExtra(AppConstants.USER_NAME_S), i.getStringExtra(AppConstants.USER_NAME_R), FirebaseAuth.getInstance().getCurrentUser().getUid(), i.getStringExtra(AppConstants.USER_ID_R), input.getText().toString(), Calendar.getInstance().getTimeInMillis());
                    sendMessageToFirebaseUser(getApplicationContext(), chat, "");
                    input.setText("");

                }


            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        chat_list = (RecyclerView) findViewById(R.id.chat_list);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);

        chat_list.setLayoutManager(mLayoutManager);
        chats = new ArrayList<>();

        mChatAdapter = new ChatAdapter(getApplicationContext(),chats);

        chat_list.setAdapter(mChatAdapter);


        getMessageFromFirebaseUser(FirebaseAuth.getInstance().getCurrentUser().getUid(),i.getStringExtra(AppConstants.USER_ID_R));

        mFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void sendMessageToFirebaseUser(final Context context,
                                          final Chat chat,
                                          final String receiverFirebaseToken) {
        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e("EEE", "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e("EEE", "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_2)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        } else {
                            Log.e("EEE", "sendMessageToFirebaseUser: success");
                            databaseReference.child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });
    }

    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e("EEE", "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                            chats.add(chat);
                                            mChatAdapter.notifyDataSetChanged();
                                            chat_list.scrollToPosition(mChatAdapter.mDataset.size()-1);

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e("EEE", "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(Constants.ARG_CHAT_ROOMS)
                                    .child(room_type_2)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                            chats.add(chat);
                                            mChatAdapter.notifyDataSetChanged();
                                            chat_list.scrollToPosition(mChatAdapter.mDataset.size()-1);

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else {
                            Log.e("EEE", "getMessageFromFirebaseUser: no such room available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }





    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private ArrayList<Chat> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView name;
            public TextView message;
            public TextView timestamp;
            public ViewHolder(View v, TextView name, TextView message, TextView timestamp) {
                super(v);
                mView = v;
                this.name = name;
                this.message = message;
                this.timestamp = timestamp;
            }


        }

        class ViewHolder2 extends RecyclerView.ViewHolder {

            public View mView;
            public TextView name;
            public TextView message;
            public TextView timestamp;
            public ImageView user_image;

            public ViewHolder2(View v, TextView name, TextView message, TextView timestamp, ImageView user_image) {
                super(v);
                mView = v;
                this.name = name;
                this.message = message;
                this.timestamp = timestamp;
                this.user_image = user_image;
            }

        }




        public ChatAdapter(Context context, ArrayList<Chat> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {

            if (mDataset.get(position).senderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                return 0;
            else
                return 1;

        }

        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType){

                case 0:

                    View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message,parent,false);
                    //TextView ID = v1.findViewById(R.id.ID);
                    TextView name = v1.findViewById(R.id.name);
                    TextView message = v1.findViewById(R.id.message);
                    TextView timestamp = v1.findViewById(R.id.timestamp);


                    ViewHolder vh = new ViewHolder(v1,name,message, timestamp);
                    return vh;

                case 1:

                    View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_message,parent,false);
                    //TextView ID2 = v2.findViewById(R.id.ID);
                    TextView name2 = v2.findViewById(R.id.name);
                    TextView message2 = v2.findViewById(R.id.message);
                    TextView timestamp2 = v2.findViewById(R.id.timestamp);


                    ViewHolder vh2 = new ViewHolder(v2,name2,message2, timestamp2);
                    return vh2;

            }

            return null;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.name.setText(mDataset.get(position).sender);
            holder.timestamp.setText(mFormat.format(new Date(mDataset.get(position).timestamp)));
            holder.message.setText(mDataset.get(position).message);



        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }






}
