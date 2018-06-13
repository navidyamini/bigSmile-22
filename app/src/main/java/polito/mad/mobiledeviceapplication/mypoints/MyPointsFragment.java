package polito.mad.mobiledeviceapplication.mypoints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.books.ShowUserDialogFragment;
import polito.mad.mobiledeviceapplication.utils.Comment;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;

/**
 * Created by user on 24/04/2018.
 */

public class MyPointsFragment extends Fragment {

    private RatingBar user_rating;
    private ListView comment_list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_mypoints, container, false);

        ((MainActivity)getActivity()).toolbar.setTitle(R.string.points);

        user_rating = (RatingBar) rootView.findViewById(R.id.userRating);
        comment_list = (ListView) rootView.findViewById(R.id.commentsListView);




        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity a=getActivity();
        if (a instanceof ShowUserDialogFragment.FragUserObserver) {
            ShowUserDialogFragment.FragUserObserver observer = (ShowUserDialogFragment.FragUserObserver) a;
            Intent intent = new Intent(Constants.GET_USER_INFO);
            observer.getUserInformation(intent);
        }
    }

    public void retrieveUserInformation(User user, float rating, ArrayList<Comment> comments){

        user_rating.setRating(rating);
        comment_list.setAdapter(new MyPointsFragment.MyCommentAdapter(getContext(),comments));


    }

    class MyCommentAdapter extends BaseAdapter {

        private ArrayList<Comment> mDataset;
        private Context context;

        public MyCommentAdapter(Context context,ArrayList<Comment> mDataset) {
            this.mDataset = mDataset;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mDataset.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataset.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null)
            {
                convertView=LayoutInflater.from(getContext()).inflate(R.layout.item_comments, null);
            }

            TextView userComment = (TextView) convertView.findViewById(R.id.userComment);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            final TextView username = (TextView) convertView.findViewById(R.id.userNameText);
            final ImageView userImage = (ImageView) convertView.findViewById(R.id.userImage);

            ratingBar.setEnabled(false);

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child("users").child(mDataset.get(position).writer_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.username);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (mDataset.get(position).message.equals(""))
                userComment.setText("(This user didn't leave any message)");
            else
                userComment.setText(mDataset.get(position).message);

            ratingBar.setRating(mDataset.get(position).rate);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

            final long ONE_MEGABYTE = 1024 * 1024;
            StorageReference storageRef = firebaseStorage.getReference().child("images").child("users").child(mDataset.get(position).writer_id+".png");

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    userImage.setImageBitmap(bmp);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors


                }
            });



            return convertView;
        }
    }




}
