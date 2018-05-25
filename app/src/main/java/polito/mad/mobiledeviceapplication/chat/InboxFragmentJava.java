package polito.mad.mobiledeviceapplication.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Chat;
import polito.mad.mobiledeviceapplication.utils.Constants;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Created by Asus on 5/24/2018.
 */

public class InboxFragmentJava extends Fragment {

    private DatabaseReference myDatabase;
    private RelativeLayout name_inbox;
    private RecyclerView chat_list;
    private LinearLayoutManager mLayoutManager;
    private String userid;
    private ArrayList<String> chat_user_ids;
    private ArrayList<String> chat_user_names;
    private InboxAdapter inboxAdapter;

    public interface FragChatObserver {
        void notifyChatRequest(Intent intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inbox, container, false);

        ((MainActivity)getActivity()).toolbar.setTitle(R.string.inbox);

        chat_list = (RecyclerView) rootView.findViewById(R.id.chat_list);
        chat_user_ids = new ArrayList<String>();
        chat_user_names = new ArrayList<String>();

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        chat_list.setLayoutManager(mLayoutManager);

        inboxAdapter= new InboxAdapter(getContext(),chat_user_names);
        Intent intent = getActivity().getIntent();

        chat_list.setAdapter(inboxAdapter);

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS);

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String receiverUid = "null";
                        String senderUid = "null";
                        String receiverName = "null";
                        String senderName = "null";
                        String key = "null";
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot chat : dataSnapshot.getChildren()) {

                                for (DataSnapshot message : chat.getChildren()) {

                                    for (DataSnapshot field : message.getChildren()) {
                                        key = field.getKey();

                                        if (key.equals("receiverUid")) {
                                            receiverUid = (String) field.getValue();
                                        }
                                        if (key.equals("senderUid")) {
                                            senderUid = (String) field.getValue();
                                        }
                                        if (key.equals("receiver")) {
                                            receiverName= (String) field.getValue();
                                        }
                                        if (key.equals("sender")) {
                                            senderName = (String) field.getValue();
                                        }
                                    }

                                }
                                if (userid.equals(receiverUid)) {
                                    chat_user_ids.add(senderUid);
                                    chat_user_names.add(senderName);
                                }
                                if (userid.equals(senderUid)) {
                                    chat_user_ids.add(receiverUid);
                                    chat_user_names.add(receiverName);
                                }
                            }
                        }

                        inboxAdapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        return rootView;
    }

    class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

        private ArrayList<String> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView userName;
            public ViewHolder(View v, TextView name) {
                super(v);
                mView = v;
                this.userName = name;
            }


        }

        public InboxAdapter(Context context, ArrayList<String> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list,parent,false);
            //TextView ID = v1.findViewById(R.id.ID);
            TextView name = v1.findViewById(R.id.name_inbox);

            ViewHolder vh = new ViewHolder(v1,name);
            return vh;

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.userName.setText(mDataset.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity a=getActivity();
                    if (a instanceof InboxFragmentJava.FragChatObserver) {
                        InboxFragmentJava.FragChatObserver observer = (InboxFragmentJava.FragChatObserver) a;
                        Intent intent = new Intent(Constants.Chat_Request);
                        intent.putExtra("user_id_r",chat_user_ids.get(position));
                        intent.putExtra("username_r",chat_user_names.get(position));
                        observer.notifyChatRequest(intent);
                    }

                }
            });
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }


}

