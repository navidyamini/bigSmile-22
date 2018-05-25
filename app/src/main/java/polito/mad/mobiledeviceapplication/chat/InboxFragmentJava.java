package polito.mad.mobiledeviceapplication.chat;

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
    private InboxAdapter inboxAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inbox, container, false);

        ((MainActivity)getActivity()).toolbar.setTitle(R.string.inbox);

        chat_list = (RecyclerView) rootView.findViewById(R.id.chat_list);
        name_inbox = (RelativeLayout) rootView.findViewById(R.id.name_inbox);
        chat_user_ids = new ArrayList<String>();
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        chat_list.setLayoutManager(mLayoutManager);
        inboxAdapter= new InboxAdapter(getContext(),chat_user_ids);
        Intent intent = getActivity().getIntent();

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS);

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String receiverUid = "null";
                        String senderUid = "null";
                        String key = "null";
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot chat : dataSnapshot.getChildren()) {
                                for (DataSnapshot message : chat.getChildren().iterator().next().getChildren()) {
                                    key = message.getKey();
                                    if(key.equals( "receiverUid")){
                                        receiverUid = (String) message.getValue();
                                    }
                                    if(key.equals("senderUid")){
                                        senderUid = (String) message.getValue();
                                    }
                                }
                                if(userid.equals(receiverUid)){
                                    chat_user_ids.add(senderUid);
                                    inboxAdapter.notifyDataSetChanged();
                                }
                                if(userid.equals(senderUid)){
                                    chat_user_ids.add(receiverUid);
                                    inboxAdapter.notifyDataSetChanged();
                                }
                            }
                        }
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
                this.userName = userName;
            }


        }

        public InboxAdapter(Context context, ArrayList<String> myDataset) {
            mDataset = myDataset;
            this.context = context;
        }

        @Override
        public InboxFragmentJava.InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



                    View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list,parent,false);
                    //TextView ID = v1.findViewById(R.id.ID);
                    TextView name = v1.findViewById(R.id.name_inbox);

                    InboxFragmentJava.InboxAdapter.ViewHolder vh = new InboxFragmentJava.InboxAdapter.ViewHolder(v1,name);
                    return vh;

        }

        @Override
        public void onBindViewHolder(final InboxFragmentJava.InboxAdapter.ViewHolder holder, final int position) {

            holder.userName.setText(mDataset.get(position));

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }






}

