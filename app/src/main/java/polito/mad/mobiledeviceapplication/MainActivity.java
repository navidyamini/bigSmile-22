package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import polito.mad.mobiledeviceapplication.books.AddBookDialogFragment;
import polito.mad.mobiledeviceapplication.loginsignin.LoginSigninActivity;
import polito.mad.mobiledeviceapplication.loginsignin.MailDialogFragment;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;

/**
 * Created by user on 22/04/2018.
 */

public class MainActivity extends FragmentActivity implements AddBookDialogFragment.FragBookObserver {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Button disconnect;
    private Button insert_book;
    private Button scan_book;

    @Override
    public void notifyActionBook(Intent intent) {

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

            Book book = new Book(isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags);

            final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentByTag("AddBookDialog");
            ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText("We are performing the insertion of your book inside our database. Please wait...");
            fragment.setFormEnabled(false);

            if (mAuth.getCurrentUser() == null) {

                mDatabase.child("users").child(getSharedPreferences(Constants.PREFERENCE_FILE, MODE_PRIVATE).getString("UID", "")).child("books").push().setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            System.out.println("BOOK INSERTED");
                            if (fragment.isAdded() && fragment.isVisible()) {
                                fragment.dismiss();
                            }

                            }

                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.setFormEnabled(true);
                            ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText("This book will be available in your library and you will be able to share it with your friends.");
                        }
                    }
                });
            }
             else {

                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("books").push().setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            System.out.println("BOOK INSERTED");
                            if (fragment.isAdded() && fragment.isVisible()) {
                                fragment.dismiss();
                            }
                        }

                        if (fragment.isVisible() && fragment.isAdded()) {
                            fragment.setFormEnabled(true);
                            ((TextSwitcher) fragment.getView().findViewById(R.id.explaination_switcher)).setText("This book will be available in your library and you will be able to share it with your friends.");
                        }
                    }
                });
            }

        } else if (Constants.SCAN_BOOK.equals(intent.getAction())) {

            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            scanIntegrator.setPrompt("Scan the barcode of your book to retrieve its information online");
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

        insert_book =(Button) findViewById(R.id.insert_book_btn);
        disconnect = (Button) findViewById(R.id.disconnect);
        scan_book = (Button) findViewById(R.id.scan_book_btn);

        mAuth = FirebaseAuth.getInstance();

        insert_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBookDialogFragment addBookDialogFragment = new AddBookDialogFragment();
                addBookDialogFragment.show(getSupportFragmentManager(),"AddBookDialog");

            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("UID","").apply();
                Intent i = new Intent(getApplicationContext(), LoginSigninActivity.class);
                startActivity(i);
                finish();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                final String isbn = result.getContents();

                final AddBookDialogFragment fragment = (AddBookDialogFragment)getSupportFragmentManager().findFragmentByTag("AddBookDialog");

                if (fragment.isVisible() && fragment.isAdded())
                    ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText("We are retrieving information about your book on the Internet. Please wait...");




                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_BOOKS + isbn + "&projection=full",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONObject object = new JSONObject(response);
                                    String title = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getString("title");
                                    String publisher = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getString("publisher");
                                    String edition_year = ((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getString("publishedDate");

                                    StringBuilder genre = new StringBuilder();

                                    for (int i=0;i<((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getJSONArray("categories").length();i++)
                                        genre.append(((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getJSONArray("categories").get(i).toString() + ",");


                                    StringBuilder authors = new StringBuilder();

                                    for (int i=0;i<((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getJSONArray("authors").length();i++)
                                        authors.append(((JSONObject)((JSONObject)((JSONArray)object.get("items")).get(0)).get("volumeInfo")).getJSONArray("authors").get(i).toString() + ",");

                                    System.out.println("TITLE " + title);

                                    if (fragment.isVisible() && fragment.isAdded()) {
                                        fragment.setFields(isbn, title, authors.deleteCharAt(authors.length() - 1).toString(), publisher, "", edition_year, genre.deleteCharAt(genre.length() - 1).toString(), "");
                                        fragment.setFormEnabled(true);
                                        ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText("This book will be available in your library and you will be able to share it with your friends.");

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this, "Error while retrieving data from server", Toast.LENGTH_SHORT).show();

                        if (fragment.isAdded() && fragment.isVisible()) {
                            fragment.setFormEnabled(true);
                            ((TextSwitcher)fragment.getView().findViewById(R.id.explaination_switcher)).setText("This book will be available in your library and you will be able to share it with your friends.");

                        }
                    }
                });

                if (fragment.isAdded() && fragment.isVisible())
                    fragment.setFormEnabled(false);

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
}