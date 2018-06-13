package polito.mad.mobiledeviceapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import polito.mad.mobiledeviceapplication.books.AddBookDialogFragment;
import polito.mad.mobiledeviceapplication.books.MyBooksFragment;
import polito.mad.mobiledeviceapplication.books.ShowBookDialogFragment;
import polito.mad.mobiledeviceapplication.books.ShowUserDialogFragment;
import polito.mad.mobiledeviceapplication.chat.ChatActivity;
import polito.mad.mobiledeviceapplication.chat.InboxFragment;
import polito.mad.mobiledeviceapplication.home.HomeFragment;
import polito.mad.mobiledeviceapplication.loginsignin.LoginSignupActivity;
import polito.mad.mobiledeviceapplication.profile.ShowProfileActivity;
import polito.mad.mobiledeviceapplication.requests.RequestsFragment;
import polito.mad.mobiledeviceapplication.search.SearchForm;
import polito.mad.mobiledeviceapplication.search.SearchFragment;
import polito.mad.mobiledeviceapplication.search.SearchMap;
import polito.mad.mobiledeviceapplication.services.ChatService;
import polito.mad.mobiledeviceapplication.settings.SettingsFragment;
import polito.mad.mobiledeviceapplication.utils.AppConstants;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Comment;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.MyRequest;
import polito.mad.mobiledeviceapplication.utils.User;

/**
 * Created by user on 22/04/2018.
 */

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeObserver,AddBookDialogFragment.FragBookObserver,SearchForm.FragSearchObserver, ShowBookDialogFragment.FragContactObserver, InboxFragment.FragChatObserver, RequestsFragment.RequestObserver, ShowUserDialogFragment.FragUserObserver {

    public FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public FirebaseStorage firebaseStorage;

    private Button disconnect;
    private Button insert_book;
    private Button scan_book;

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;
    public android.support.v7.widget.Toolbar toolbar;


    @Override
    public void retrieveImage(@NotNull Intent intent) {

        if (Constants.RETRIEVE_IMAGE.equals(intent.getAction())) {

//            if (firebaseStorage==null)
//                firebaseStorage = FirebaseStorage.getInstance();
//
//
//            if (firebaseStorage==null)
//                firebaseStorage = FirebaseStorage.getInstance();
//
//            final long ONE_MEGABYTE = 1024 * 1024;
//            StorageReference storageRef = firebaseStorage.getReference().child("images").child("users").child(intent.getStringExtra("user_id")+".png");
//
//            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//
//                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    final InboxFragment fragment = (InboxFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
//
//                    if (fragment.isAdded() && fragment.isVisible()) {
//                        //((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
//                        fragment.retrieveBookInformation(book,bmp);
//                    }
//
//
//
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//
//
//                }
//            });
//
//
//        }
        }

    }

    @Override
    public void retrieveBook(final Intent intent) {

        if (Constants.RETRIEVE_BOOK.equals(intent.getAction())){

            mAuth = FirebaseAuth.getInstance();

            System.out.println(getIntent().getStringExtra("book_id"));
            final String userID = mAuth.getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(userID).child("books").child(intent.getStringExtra("book_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Book book = dataSnapshot.getValue(Book.class);


                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageRef = storage.getReference().child("images").child("books").child(intent.getStringExtra("book_id"));

                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

                            if (fragment.isAdded() && fragment.isVisible()) {
                                //((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
                                fragment.setFormEnabled(true);
                                fragment.retrieveBookInformation(book,bmp);
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors

                            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

                            if (fragment.isAdded() && fragment.isVisible()) {
                                //((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
                                fragment.setFormEnabled(true);
                                fragment.retrieveBookInformation(book,null);
                            }
                        }
                    });



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("aaa", "loadPost:onCancelled", databaseError.toException());
                }
            });





        }


    }

    @Override
    public void getUserInformation(Intent intent) {

        if (Constants.GET_USER_INFO.equals(intent.getAction())){

            final String user_id = intent.getStringExtra("user_id");

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final User user = dataSnapshot.getValue(User.class);
                    double final_rating = 0;
                    int count = 0;
                    ArrayList<Comment> comments = new ArrayList<>();
                    for (DataSnapshot comment : dataSnapshot.child("comments").getChildren()){

                        if (!comment.child("message").toString().equals("")) {
                            comments.add(comment.getValue(Comment.class));
                            final_rating = final_rating + Float.parseFloat(comment.child("rate").getValue().toString());
                            count++;
                        }


                    }
                    float avg_rating = 0;
                    if (count>0) {
                        avg_rating = (float) final_rating / count;
                    }

                    ShowUserDialogFragment fragment = (ShowUserDialogFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame).
                                getChildFragmentManager().findFragmentByTag("ShowBookDialog").getChildFragmentManager().findFragmentByTag("ShowUserDialog");

                    if (fragment != null && fragment.isVisible()) {

                        fragment.retrieveUserInformation(user, avg_rating, comments);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void rateRequest(Intent intent) {

        if (Constants.RATE_REQUEST_BORROWER.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");
            final String other_user = intent.getStringExtra("user_id");
            final String book_id = intent.getStringExtra("book_id");


            final String book_comment = intent.getStringExtra("book_comment");
            final String user_comment = intent.getStringExtra("user_comment");

            final Float book_rating = intent.getFloatExtra("book_rating",0f);
            final Float user_rating = intent.getFloatExtra("user_rating",0f);

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            Comment mUserComment = new Comment(user_comment,user_rating,mAuth.getCurrentUser().getUid());

            mDatabase.child("users").child(other_user).child("comments").push().setValue(mUserComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Comment mBookComment = new Comment(book_comment,book_rating,mAuth.getCurrentUser().getUid());
                    mDatabase.child("users").child(other_user).child("books").child(book_id).child("comments").push().setValue(mBookComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(),R.string.feedback,Toast.LENGTH_LONG).show();

                        }
                    });


                }
            });




        } else if (Constants.RATE_REQUEST_OWNER.equals(intent.getAction())){


            final String req_id = intent.getStringExtra("request_id");
            final String other_user = intent.getStringExtra("user_id");

            final String user_comment = intent.getStringExtra("user_comment");

            final Float user_rating = intent.getFloatExtra("user_rating",0f);

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            Comment mUserComment = new Comment(user_comment,user_rating,mAuth.getCurrentUser().getUid());

            mDatabase.child("users").child(other_user).child("comments").push().setValue(mUserComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),R.string.feedback,Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    public void sentbackRequest(Intent intent) {

        if (Constants.SENTBACK_REQUEST.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");
            final String other_user = intent.getStringExtra("username");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(other_user).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)){

                                HashMap<String,Object> map = (HashMap<String, Object>) request.getValue(MyRequest.class).toMap();
                                map.put("status",MyRequest.STATUS.SENT_BACK);
                                request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        searchRequests(new Intent(Constants.SEARCH_OUTGOING_REQUESTS));
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }

    }

    @Override
    public void receivedRequest(Intent intent) {

        if (Constants.RECEIVED_REQUEST.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");
            final String other_user = intent.getStringExtra("username");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(other_user).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)){

                                HashMap<String,Object> map = (HashMap<String, Object>) request.getValue(MyRequest.class).toMap();
                                map.put("status",MyRequest.STATUS.RECEIVED);
                                request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        searchRequests(new Intent(Constants.SEARCH_OUTGOING_REQUESTS));
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void contactBorrower(Intent intent) {

        if (Constants.CHAT_REQUEST.equals(intent.getAction())){

            final String user_id = intent.getStringExtra("user_id_r");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                    i.putExtra(AppConstants.USER_ID_S,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    i.putExtra(AppConstants.USER_ID_R,user_id);
                    i.putExtra(AppConstants.USER_USERNAME_R,dataSnapshot.getValue(User.class).username);
                    i.putExtra(AppConstants.USER_USERNAME_S,getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username",""));
                    startActivity(i);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void endRequest(Intent intent) {

        if (Constants.END_REQUEST.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)){

                                HashMap<String,Object> map = (HashMap<String, Object>) request.getValue(MyRequest.class).toMap();
                                map.put("status",MyRequest.STATUS.ENDED);
                                request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        searchRequests(new Intent(Constants.SEARCH_INCOMING_REQUESTS));
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

    }

    @Override
    public void startLending(Intent intent) {

        if (Constants.START_LENDING.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)){

                                HashMap<String,Object> map = (HashMap<String, Object>) request.getValue(MyRequest.class).toMap();
                                map.put("status",MyRequest.STATUS.SENT);
                                request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        searchRequests(new Intent(Constants.SEARCH_INCOMING_REQUESTS));
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void deleteRequest(Intent intent) {

        if (Constants.DELETE_REQUEST.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)) {
                                request.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        RequestsFragment fragment = (RequestsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

                                        if (fragment!=null && fragment.isVisible()){

                                            searchRequests(new Intent(Constants.SEARCH_INCOMING_REQUESTS));
                                            searchRequests(new Intent(Constants.SEARCH_OUTGOING_REQUESTS));

                                        }
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (Constants.REJECT_REQUEST.equals(intent.getAction())){


            final String req_id = intent.getStringExtra("request_id");
            String other_user = intent.getStringExtra("username");
            if (other_user==null)
                other_user = mAuth.getCurrentUser().getUid();

            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(other_user).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot book : dataSnapshot.getChildren()){

                            for (DataSnapshot request : book.child("requests").getChildren()){

                                if (request.getKey().equals(req_id)){

                                    MyRequest r = request.getValue(MyRequest.class);
                                    HashMap<String,Object> map = (HashMap)r.toMap();
                                    map.put("status",MyRequest.STATUS.REJECTED.toString());
                                    request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            RequestsFragment fragment = (RequestsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

                                            if (fragment!=null && fragment.isVisible()) {
                                                searchRequests(new Intent(Constants.SEARCH_INCOMING_REQUESTS));
                                                searchRequests(new Intent(Constants.SEARCH_OUTGOING_REQUESTS));
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void acceptRequest(Intent intent) {

        if (Constants.ACCEPT_REQUEST.equals(intent.getAction())){

            final String req_id = intent.getStringExtra("request_id");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                        for (DataSnapshot request : book.child("requests").getChildren()){

                            if (request.getKey().equals(req_id)){

                                HashMap<String,Object> map = (HashMap<String, Object>) request.getValue(MyRequest.class).toMap();
                                map.put("status",MyRequest.STATUS.ACCEPTED);
                                request.getRef().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        searchRequests(new Intent(Constants.SEARCH_INCOMING_REQUESTS));
                                    }
                                });
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void searchRequests(Intent intent) {

        if (Constants.SEARCH_OUTGOING_REQUESTS.equals(intent.getAction())){

            final ArrayList<HashMap> list = new ArrayList<>();
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot user : dataSnapshot.getChildren()){

                        for (DataSnapshot book : user.child("books").getChildren()){

                            for (DataSnapshot request : book.child("requests").getChildren()){

                                if (request.getValue(MyRequest.class).requester_id!=null) {
                                    if (request.getValue(MyRequest.class).requester_id.equals(mAuth.getCurrentUser().getUid()) &&
                                            !(request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.REJECTED.name()) ||
                                            request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.ENDED.name()))) {


                                        MyRequest r = (request.getValue(MyRequest.class));
                                        HashMap<String,Object> map = new HashMap<>();

                                        map.put("request",r);
                                        map.put("book_id",book.getKey());
                                        map.put("book_name",book.getValue(Book.class).title);
                                        map.put("username",user.getValue(User.class).username);
                                        map.put("request_id",request.getKey());
                                        map.put("owner_id",user.getKey());
                                        list.add(map);
                                    }
                                }
                            }
                        }
                    }

                    RequestsFragment f= (RequestsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (f!=null && f.isVisible()) {
                        f.sendOutgoingData(list);

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        } else if (Constants.SEARCH_INCOMING_REQUESTS.equals(intent.getAction())){

            final ArrayList<HashMap> list = new ArrayList<>();
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot book : dataSnapshot.getChildren()){

                            for (DataSnapshot request : book.child("requests").getChildren()){

                                if (request.getValue(MyRequest.class).requester_id!=null) {

                                    if (!(request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.REJECTED.name()) ||
                                            request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.ENDED.name()))){


                                        MyRequest r = (request.getValue(MyRequest.class));

                                        HashMap<String,Object> map = new HashMap<>();

                                        map.put("request",r);
                                        map.put("book_id",book.getKey());
                                        map.put("book_name",book.getValue(Book.class).title);
                                        map.put("request_id",request.getKey());


                                        list.add(map);
                                    }
                                }
                            }
                        }


                    RequestsFragment f= (RequestsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (f!=null && f.isVisible()) {
                        f.setIncomingData(list);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }



    @Override
    public void notifyBorrowRequest(Intent intent) {

        if (Constants.BORROW_REQUEST.equals(intent.getAction())){

            if (mDatabase==null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            final MyRequest r = new MyRequest(intent.getStringExtra("start_date"),intent.getStringExtra("end_date"),intent.getStringExtra("comments"), mAuth.getCurrentUser().getUid(),MyRequest.STATUS.WAIT.name());


            final DatabaseReference insertRef = mDatabase.
                    child("users").
                    child(intent.getStringExtra("user_id_r")).
                    child("books").
                    child(intent.getStringExtra("book_id")).child("requests");

            insertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean request_found = false;
                        for (DataSnapshot request : dataSnapshot.getChildren()) {

                            System.out.println("R "+ request.getValue(MyRequest.class).requester_id);
                            if (request.getValue(MyRequest.class).requester_id.equals(r.requester_id) && !(request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.ENDED.name())||request.getValue(MyRequest.class).status.equals(MyRequest.STATUS.REJECTED.name()))) {
                                request_found = true;
                            }
                        }
                        if (!request_found) {
                            insertRef.push().setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    System.out.println("Inserted requests");
                                }
                            });

                        }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }


    }

    @Override
    public void notifyChatRequest(Intent intent) {
        if (Constants.CHAT_REQUEST.equals(intent.getAction())){
            Intent i = new Intent(getApplicationContext(),ChatActivity.class);
            i.putExtra(AppConstants.USER_ID_S,FirebaseAuth.getInstance().getCurrentUser().getUid());
            i.putExtra(AppConstants.USER_ID_R,intent.getStringExtra("user_id_r"));
            i.putExtra(AppConstants.USER_USERNAME_R,intent.getStringExtra("username_r"));
            i.putExtra(AppConstants.USER_USERNAME_S,getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username",""));
            startActivity(i);
        }
    }

    @Override
    public void searchBooks(Intent intent) {

        if (Constants.SEARCH_PREFS.equals(intent.getAction())){

            if (mDatabase==null)
                mDatabase = FirebaseDatabase.getInstance().getReference();


            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<HashMap<String, Object>> book_list = new ArrayList();
                    HashMap<String, Bundle> h = new HashMap<>();
                    ArrayList<HashMap<String, Object>> comments = new ArrayList<>();

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.child("users").getChildren()) {

                            if (!child.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                Bundle b = new Bundle();

                                for (DataSnapshot books : child.child("books").getChildren()) {


                                    for (DataSnapshot comment : books.child("comments").getChildren()) {


                                        comments.add((HashMap<String, Object>) comment.getValue(Comment.class).toMap());
                                    }
                                    Book book_element = books.getValue(Book.class);

                                    if (book_element != null) {
                                        //if (book_element.genre.equals(getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getStringSet("pref_genre",new android.support.v4.util.ArraySet<String>())))
                                        HashMap<String, Object> book_map = (HashMap<String, Object>) book_element.toMap();
                                        book_map.put("book_id", books.getKey());
                                        book_map.put("comments", comments.clone());
                                        book_list.add(book_map);

                                        comments.clear();
                                    }

                                }


                                if (!book_list.isEmpty()) {
                                    b.putStringArrayList("book_list", (ArrayList) book_list.clone());
                                    b.putSerializable("user", (HashMap) child.getValue(User.class).toMap());
                                    h.put(child.getKey(), b);
                                }
                                b = null;
                                book_list.clear();

                            }
                        }


                        Bundle b1 = new Bundle();
                        b1.putSerializable("arg", h);

                        HomeFragment f= (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        if (f!=null && f.isVisible()) {
                            f.updatePrefs(b1);

                        }


                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();

                    }
                }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w("firebase", databaseError.toException());

                    }
                };

            mDatabase.addListenerForSingleValueEvent(postListener);




            }

    }

    @Override
    public void notifySearchRequest(Intent intent) {
        if(Constants.SEARCH_RESULT.equals(intent.getAction())){


            if (mDatabase==null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            final String title = intent.getStringExtra("title");
            final String author = intent.getStringExtra("author");
            final String genre = intent.getStringExtra("genre");
            final String publisher = intent.getStringExtra("publisher");




            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<HashMap<String,Object>> book_list = new ArrayList();
                    HashMap<String,Bundle> h = new HashMap<>();
                    ArrayList<HashMap<String,Object>> comments = new ArrayList<>();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.child("users").getChildren()) {

                            Bundle b = new Bundle();

                            if (!child.getKey().equals(mAuth.getCurrentUser().getUid())) {

                                for (DataSnapshot books : child.child("books").getChildren()) {
                                    Book book_element = books.getValue(Book.class);
                                    if (book_element != null) {


                                        if ((title.equals("") && genre.equals("") && publisher.equals("") && author.equals("")) ||
                                                ((book_element.title.toLowerCase().contains(title.toLowerCase()) && title.length()>0)||
                                                        (book_element.genre.toLowerCase().contains(genre.toLowerCase()) && genre.length()>0)||
                                                        (book_element.publisher.toLowerCase().contains(publisher.toLowerCase()) && publisher.length()>0) ||
                                                        (book_element.author.toLowerCase().contains(author.toLowerCase()) && author.length()>0))) {

                                            comments.clone();
                                            for (DataSnapshot comment : books.child("comments").getChildren()) {
                                                comments.add((HashMap<String, Object>) comment.getValue(Comment.class).toMap());
                                            }

                                            HashMap<String, Object> book_map = (HashMap<String, Object>) book_element.toMap();
                                            book_map.put("book_id", books.getKey());
                                            book_map.put("comments", comments.clone());
                                            book_list.add(book_map);

                                        }
                                    }
                                }
                            }

                            if (!book_list.isEmpty()) {
                                b.putStringArrayList("book_list", (ArrayList)book_list.clone());
                                b.putSerializable("user",(HashMap)child.getValue(User.class).toMap());
                                h.put(child.getKey(),b);
                            }

                            b = null;
                            book_list.clear();

                        }

                        Bundle b1 = new Bundle();
                        b1.putSerializable("arg",h);
                        SearchMap fragment = new SearchMap();
                        fragment.setArguments(b1);

                        FragmentTransaction transaction = getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    } else {

                        Toast.makeText(getApplicationContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w("firebase", databaseError.toException());

                }
            };

            mDatabase.addListenerForSingleValueEvent(postListener);

        }
    }

    @Override
    public void notifyActionBook(final Intent intent) {

        if (Constants.NEW_BOOK.equals(intent.getAction())) {

            System.out.println("NEW BOOK");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            String title = intent.getStringExtra("title");
            String author = intent.getStringExtra("author");
            String edition_year = intent.getStringExtra("edition_year");
            String book_conditions = intent.getStringExtra("book_conditions");
            String publisher = intent.getStringExtra("publisher");
            String isbn = intent.getStringExtra("ISBN");
            String genre = intent.getStringExtra("genre");
            String extra_tags = intent.getStringExtra("extra_tags");
            String image_url = intent.getStringExtra("image_url");
            String insert_time = String.valueOf(new Date().getTime());

            Book book = new Book(isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags, image_url,insert_time);

            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

            if (fragment.isAdded() && fragment.isVisible()) {
                ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
                fragment.setFormEnabled(false);
            }



            final DatabaseReference insertRef = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").push();
            insertRef.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").child(insertRef.getKey()).child("requests").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if (dataSnapshot!=null)
                                    dataSnapshot.getValue(MyRequest.class);

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

                            }
                        });

                        System.out.println("BOOK INSERTED");
                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.dismiss();
                            Toast.makeText(getApplicationContext(),getString(R.string.insert_complete),Toast.LENGTH_LONG).show();

                        }

                        if (intent.hasExtra("book_bitmap")) {
                            Bitmap bitmap = intent.getParcelableExtra("book_bitmap");
                            StorageReference storageRef = firebaseStorage.getReference().child("images").child("books").child(insertRef.getKey() + ".png");

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = storageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    System.out.println("EXCEPTION " + exception);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    System.out.println("Book image inserted into Cloud Storage");
                                }
                            });
                        }
                    }

                    if (fragment.isVisible() && fragment.isAdded()) {
                        fragment.setFormEnabled(true);
                        ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));
                    }
                }
            });



        } else if (Constants.SCAN_BOOK.equals(intent.getAction())) {


            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            scanIntegrator.setPrompt(getString(R.string.barcode_scan));
            scanIntegrator.setBeepEnabled(false);
            scanIntegrator.setOrientationLocked(true);
            scanIntegrator.setBarcodeImageEnabled(true);
            scanIntegrator.initiateScan();

        } else if (Constants.EDIT_BOOK.equals(intent.getAction())){


            System.out.println("EDIT BOOK");
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            String title = intent.getStringExtra("title");
            String author = intent.getStringExtra("author");
            String edition_year = intent.getStringExtra("edition_year");
            String book_conditions = intent.getStringExtra("book_conditions");
            String publisher = intent.getStringExtra("publisher");
            String isbn = intent.getStringExtra("ISBN");
            String genre = intent.getStringExtra("genre");
            String extra_tags = intent.getStringExtra("extra_tags");
            String image_url = intent.getStringExtra("image_url");
            String insert_time = String.valueOf(new Date().getTime());

            Book book = new Book(isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags, image_url,insert_time);

            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

            if (fragment.isAdded() && fragment.isVisible()) {
                //((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
                fragment.setFormEnabled(false);
            }



            final DatabaseReference insertRef = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").push();
            insertRef.updateChildren(book.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").child(insertRef.getKey()).child("requests").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if (dataSnapshot!=null)
                                    dataSnapshot.getValue(MyRequest.class);

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

                            }
                        });

                        System.out.println("BOOK EDITED");
                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.dismiss();
                            Toast.makeText(getApplicationContext(),getString(R.string.edit_complete),Toast.LENGTH_LONG).show();

                        }

                        if (intent.hasExtra("book_bitmap")) {
                            Bitmap bitmap = intent.getParcelableExtra("book_bitmap");
                            StorageReference storageRef = firebaseStorage.getReference().child("images").child("books").child(insertRef.getKey() + ".png");

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = storageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    System.out.println("EXCEPTION " + exception);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    System.out.println("Book image inserted into Cloud Storage");
                                }
                            });
                        }
                    }

                    if (fragment.isVisible() && fragment.isAdded()) {
                        fragment.setFormEnabled(true);
                        ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));
                    }
                }
            });






        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerlayout,R.string.open,R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        navigation = (NavigationView) findViewById(R.id.drawer_navigation);
        //((TextView)navigation.getHeaderView(0).findViewById(R.id.user_name)).setText(getString(R.string.welcome) + " " + getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username",""));
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                mDrawerlayout.closeDrawers();
                switch(item.getItemId()) {

                    case R.id.home:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new HomeFragment()).commit();
                        return true;

                    case R.id.my_profile:

                        Intent intent = new Intent(getApplicationContext(), ShowProfileActivity.class);
                        startActivity(intent);

                        //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MyProfileFragment()).commit();

                        return true;

                    case R.id.my_books:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MyBooksFragment()).commit();

                        return true;

                    case R.id.inbox:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new InboxFragment()).commit();

                        return true;
                    case R.id.search:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new SearchFragment()).commit();

                        return true;
                    case R.id.setting:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new SettingsFragment()).commit();

                        return true;

                    case R.id.requests:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new RequestsFragment()).commit();

                        return true;
                    case R.id.exit:

                        stopService(new Intent(getApplicationContext(),ChatService.class));
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog);
                                builder.setMessage(R.string.exit_q);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("UID","").apply();
                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("username","").apply();
                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("password","").apply();

                                        if (mAuth==null)
                                            mAuth = FirebaseAuth.getInstance();

                                        if (mAuth.getCurrentUser()!=null){


                                            FirebaseAuth.getInstance().signOut();
                                            Intent i = new Intent(getApplicationContext(), LoginSignupActivity.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });


                                builder.create().show();

                            }
                        },400);





                        return true;
                    default:
                        return false;
                }

            }
        });


        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new HomeFragment());
        tx.commit();

        startService(new Intent(getApplicationContext(), ChatService.class));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "Message";
            String description = "Message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("Message", name, importance);
            mChannel.setDescription(description);
            mChannel.setSound((RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)),null);
            mChannel.setVibrationPattern(new long[]{0, 250, 250, 250});
            //mChannel.set
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);


        }


        initializeRequestsListener();





    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, R.string.insert_manually, Toast.LENGTH_LONG).show();
            } else {

                final String isbn = result.getContents();

                final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

                if (fragment!=null)
                    if (fragment.isVisible() && fragment.isAdded())
                        ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_retrieve));




                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_BOOKS + isbn + "&projection=full",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String title = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optString("title");
                                    String publisher = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optString("publisher");
                                    String edition_year = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optString("publishedDate");

                                    StringBuilder genre = new StringBuilder();

                                    for (int i=0;i<((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optJSONArray("categories").length();i++)
                                        genre.append(((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optJSONArray("categories").get(i).toString() + ",");


                                    StringBuilder authors = new StringBuilder();

                                    for (int i=0;i<((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optJSONArray("authors").length();i++)
                                        authors.append(((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optJSONArray("authors").get(i).toString() + ",");

                                    String image_url = "";
                                    try {
                                         image_url = (((JSONObject) ((JSONObject) ((JSONArray) object.get("items")).get(0)).opt("volumeInfo")).optJSONObject("imageLinks")).optString("thumbnail");
                                        //for (int i=0;i<((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).opt("volumeInfo")).optJSONArray("imageLinks").length();i++)
                                    }catch (Exception e){}


                                    if (fragment.isVisible() && fragment.isAdded()) {
                                        fragment.setFields(isbn, title, authors.deleteCharAt(authors.length() - 1).toString(), publisher, "", edition_year, genre.deleteCharAt(genre.length() - 1).toString(), "",image_url);
                                        fragment.setFormEnabled(true);
                                        ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));

                                    }

                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                                    if (fragment.isAdded() && fragment.isVisible()) {
                                        fragment.setFormEnabled(true);
                                        ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));
                                    }
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.setFormEnabled(true);
                            ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));

                        }
                    }

                });

                if (fragment.isAdded() && fragment.isVisible())
                    fragment.setFormEnabled(false);

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        retrieveUser();



    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentById(R.id.content_frame).getClass().equals(HomeFragment.class))
            moveTaskToBack(true);
        
        else if (getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentById(R.id.container)!=null){
            if (getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentById(R.id.container).getClass().equals(SearchMap.class))
                getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().beginTransaction().replace(R.id.container,new SearchForm()).commit();
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new HomeFragment()).commit();
        }
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new HomeFragment()).commit();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            if (mDrawerlayout.isDrawerOpen(Gravity.LEFT))
                if (item.getItemId()!=R.id.exit)
                    mDrawerlayout.closeDrawer(Gravity.LEFT);
            else
                mDrawerlayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void notifyContactRequest(Intent intent) {
        if (Constants.CONTACT_REQUEST.equals(intent.getAction())){
            //((ShowBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("ShowBookDialog")).dismiss();
            Intent i = new Intent(getApplicationContext(),ChatActivity.class);
            i.putExtra(AppConstants.USER_ID_S,FirebaseAuth.getInstance().getCurrentUser().getUid());
            i.putExtra(AppConstants.USER_ID_R,intent.getStringExtra("user_id_r"));
            i.putExtra(AppConstants.USER_USERNAME_R,intent.getStringExtra("username_r"));
            i.putExtra(AppConstants.USER_USERNAME_S,getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username",""));
            startActivity(i);

        }
    }


    private void initializeRequestsListener(){

        if (mDatabase==null)
            mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot book : dataSnapshot.getChildren()){


                        book.child("requests").getRef().addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if (dataSnapshot.getValue(MyRequest.class).status.equals(MyRequest.STATUS.WAIT)) {

                                    String other_user = dataSnapshot.getValue(MyRequest.class).requester_id;

                                    if (mDatabase == null)
                                        mDatabase = FirebaseDatabase.getInstance().getReference();


                                    mDatabase.child("users").child(other_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            System.out.println("ADDED REQUEST BY " + dataSnapshot.getValue(User.class).username);


                                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "Message")
                                                    .setSmallIcon(R.drawable.ic_email_white_24dp)
                                                    .setContentTitle(getString(R.string.app_name))
                                                    .setContentText(R.string.received_request + dataSnapshot.getValue(User.class).username)
                                                    .setAutoCancel(true)
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.book_request)))
                                                    .build();

                                            NotificationManager mNotificationManager =
                                                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.notify(1, notification);


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                System.out.println("MODIFIED REQUEST");

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {


                                String other_user = dataSnapshot.getValue(MyRequest.class).requester_id;

                                if (mDatabase==null)
                                    mDatabase = FirebaseDatabase.getInstance().getReference();


                                mDatabase.child("users").child(other_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        System.out.println("CHILD REMOVED BY " + dataSnapshot.getValue(User.class).username);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void retrieveUser(){

        if (mDatabase==null)
            mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                ((TextView)navigation.getHeaderView(0).findViewById(R.id.user_name)).setText(getString(R.string.welcome) + " " + user.username);

                if (firebaseStorage==null)
                    firebaseStorage = FirebaseStorage.getInstance();

                final long ONE_MEGABYTE = 1024 * 1024;
                StorageReference storageRef = firebaseStorage.getReference().child("images").child("users").child(mAuth.getCurrentUser().getUid()+".png");

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ((ImageView)navigation.getHeaderView(0).findViewById(R.id.user_image)).setImageBitmap(bmp);





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors


                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

}