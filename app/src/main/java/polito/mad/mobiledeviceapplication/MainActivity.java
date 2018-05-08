package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import polito.mad.mobiledeviceapplication.books.AddBookDialogFragment;
import polito.mad.mobiledeviceapplication.books.MyBooksFragment;
import polito.mad.mobiledeviceapplication.home.HomeFragment;
import polito.mad.mobiledeviceapplication.loginsignin.LoginSignupActivity;
import polito.mad.mobiledeviceapplication.profile.MyProfileFragment;
import polito.mad.mobiledeviceapplication.search.SearchFragment;
import polito.mad.mobiledeviceapplication.settings.SettingsFragment;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 22/04/2018.
 */

public class MainActivity extends AppCompatActivity implements AddBookDialogFragment.FragBookObserver {

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
            String isbn = intent.getStringExtra("isbn");
            String genre = intent.getStringExtra("genre");
            String extra_tags = intent.getStringExtra("extra_tags");
            String image_url = intent.getStringExtra("image_url");

            Book book = new Book(isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags, image_url);

            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

            if (fragment.isAdded() && fragment.isVisible()) {
                ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.wait_book_insert));
                fragment.setFormEnabled(false);
            }

            if (mAuth.getCurrentUser() == null) {

                final DatabaseReference insertRef = mDatabase.child("users").child(getSharedPreferences(Constants.PREFERENCE_FILE, MODE_PRIVATE).getString("UID", "")).child("books").push();
                insertRef.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
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

                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.setFormEnabled(true);
                            ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText(getString(R.string.available_book));
                        }
                    }
                });
            }
             else {

                final DatabaseReference insertRef = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").push();
                insertRef.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
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




            }







        } else if (Constants.SCAN_BOOK.equals(intent.getAction())) {

            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            scanIntegrator.setPrompt(getString(R.string.barcode_scan));
            scanIntegrator.setBeepEnabled(false);
            scanIntegrator.setOrientationLocked(true);
            scanIntegrator.setBarcodeImageEnabled(true);
            scanIntegrator.initiateScan();

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
        ((TextView)navigation.getHeaderView(0).findViewById(R.id.user_name)).setText(getString(R.string.welcome) + getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username",""));
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
                        // Handle menu click
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MyProfileFragment()).commit();

                        return true;
                    case R.id.my_books:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MyBooksFragment()).commit();

                        return true;
                    case R.id.search:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new SearchFragment()).commit();

                        return true;
                    case R.id.setting:

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new SettingsFragment()).commit();

                        return true;
                    case R.id.exit:

                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("UID","").apply();
                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("username","").apply();
                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("password","").apply();

                        if (mAuth==null)
                            mAuth = FirebaseAuth.getInstance();

                        if (mAuth.getCurrentUser()!=null){

                            if (mDatabase==null)
                                mDatabase = FirebaseDatabase.getInstance().getReference();

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    FirebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(getApplicationContext(), LoginSignupActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            });

                        } else {

                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(getApplicationContext(), LoginSignupActivity.class);
                            startActivity(i);
                            finish();

                        }

                        return true;
                    default:
                        return false;
                }

            }
        });


        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new HomeFragment());
        tx.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "It is not possible to retrieve data automatically from this book. Please insert its information manually", Toast.LENGTH_LONG).show();
            } else {

                final String isbn = result.getContents();

                final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame).getChildFragmentManager().findFragmentByTag("AddBookDialog");

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

                                } catch (JSONException e) {
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
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            if (mDrawerlayout.isDrawerOpen(Gravity.LEFT))
                mDrawerlayout.closeDrawer(Gravity.LEFT);
            else
                mDrawerlayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }
}