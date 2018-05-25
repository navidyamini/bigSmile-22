package polito.mad.mobiledeviceapplication.profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;


public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout name_text_layout, surname_text_layout, username_text_layout, email_text_layout, phone_text_layout, bio_text_layout, address_text_layout, zip_text_layout, zone_text_layout;
    private TextInputEditText name_edit, surname_edit, username_edit, email_edit, phone_edit, bio_edit, address_edit, zip_edit, zone_edit;
    public FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        if (savedInstanceState!=null)
            System.out.println(savedInstanceState.getString("name"));

        mAuth = FirebaseAuth.getInstance();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_edit);
        setSupportActionBar(myToolbar);

        name_text_layout = (TextInputLayout) findViewById(R.id.name_text_input_layout);
        surname_text_layout = (TextInputLayout) findViewById(R.id.surname_text_input_layout);
        email_text_layout = (TextInputLayout) findViewById(R.id.email_text_input_layout);
        phone_text_layout = (TextInputLayout) findViewById(R.id.phone_text_input_layout);
        bio_text_layout = (TextInputLayout) findViewById(R.id.bio_text_input_layout);
        address_text_layout = (TextInputLayout) findViewById(R.id.address_text_input_layout);
        zip_text_layout = (TextInputLayout) findViewById(R.id.zip_text_input_layout);
        zone_text_layout = (TextInputLayout) findViewById(R.id.zone_text_input_layout);
        username_text_layout = (TextInputLayout) findViewById(R.id.username_text_input_layout);


        name_edit = (TextInputEditText) findViewById(R.id.name_edit_text);
        surname_edit = (TextInputEditText) findViewById(R.id.surname_edit_text);
        email_edit = (TextInputEditText) findViewById(R.id.email_edit_text);
        phone_edit = (TextInputEditText) findViewById(R.id.phone_edit_text);
        bio_edit = (TextInputEditText) findViewById(R.id.bio_edit_text);
        address_edit = (TextInputEditText) findViewById(R.id.address_edit_text);
        zip_edit = (TextInputEditText) findViewById(R.id.zip_edit_text);
        zone_edit = (TextInputEditText) findViewById(R.id.zone_edit_text);
        username_edit = (TextInputEditText) findViewById(R.id.username_edit_text);



        retrieveUserInformation();




    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){

            System.out.println("MAIL " + savedInstanceState.getString("email"));
            name_edit.setText(savedInstanceState.getString("name"));
            surname_edit.setText(savedInstanceState.getString("surname"));
            email_edit.setText(savedInstanceState.getString("email"));
            phone_edit.setText(savedInstanceState.getString("phone"));
            bio_edit.setText(savedInstanceState.getString("bio"));
            address_edit.setText(savedInstanceState.getString("address"));
            zip_edit.setText(savedInstanceState.getString("zip"));
            zone_edit.setText(savedInstanceState.getString("zone"));


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                if (name_edit.getText().length() == 0) {
                    name_text_layout.setError(getString(R.string.not_empty));
                    name_text_layout.requestFocus();
                }
                else if (surname_edit.getText().length() == 0) {
                    surname_text_layout.setError(getString(R.string.not_empty));
                    surname_text_layout.requestFocus();

                }
                else if (address_edit.getText().length() == 0) {
                    address_text_layout.setError(getString(R.string.not_empty));
                    address_text_layout.requestFocus();

                }
                else if (zip_edit.getText().length() == 0) {
                    zip_text_layout.setError(getString(R.string.not_empty));
                    zip_text_layout.requestFocus();

                } else if (username_edit.getText().length() == 0) {

                    username_text_layout.setError(getString(R.string.not_empty));
                    username_text_layout.requestFocus();

                }
                
                else{

                    name_text_layout.setErrorEnabled(false);
                    surname_text_layout.setErrorEnabled(false);

                    if (mDatabase == null)
                        mDatabase = FirebaseDatabase.getInstance().getReference();

                    User user = new User(getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("username","2"),
                            getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("password",""),
                            name_edit.getText().toString(),
                            surname_edit.getText().toString(),
                            email_edit.getText().toString(),
                            phone_edit.getText().toString(),
                            bio_edit.getText().toString(),
                            address_edit.getText().toString(),
                            zip_edit.getText().toString(),
                            zone_edit.getText().toString());


                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {

                                getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("username",username_edit.getText().toString()).apply();

                                Toast.makeText(getApplicationContext(), getString(R.string.edit_complete), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

                            }

                            }
                        });

                }

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void retrieveUserInformation() {


        if (mDatabase==null)
            mDatabase = FirebaseDatabase.getInstance().getReference();




        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.child("users").getChildren()) {
                        if (mAuth.getCurrentUser().getUid().equals(child.getKey())) {

                            User user = child.getValue(User.class);

                            name_edit.setText(user.name);
                            surname_edit.setText(user.surname);
                            email_edit.setText(user.email);
                            phone_edit.setText(user.phone);
                            bio_edit.setText(user.bio);
                            address_edit.setText(user.address);
                            zip_edit.setText(user.ZIP);
                            zone_edit.setText(user.zone);
                            username_edit.setText(user.username);


                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("aaa", "loadPost:onCancelled", databaseError.toException());

            }
        };

        mDatabase.addListenerForSingleValueEvent(postListener);



        name_edit.setSelection(name_edit.getText().length());
        surname_edit.setSelection(surname_edit.getText().length());
        email_edit.setSelection(email_edit.getText().length());
        phone_edit.setSelection(phone_edit.getText().length());
        bio_edit.setSelection(bio_edit.getText().length());
        address_edit.setSelection(address_edit.getText().length());
        zip_edit.setSelection(zip_edit.getText().length());
        zone_edit.setSelection(zone_edit.getText().length());
        username_edit.setSelection(username_edit.getText().length());

        bio_edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        bio_edit.setRawInputType(InputType.TYPE_CLASS_TEXT);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("name",name_edit.getText().toString());
        outState.putString("surname",surname_edit.getText().toString());
        outState.putString("email",email_edit.getText().toString());
        outState.putString("phone",phone_edit.getText().toString());
        outState.putString("bio",bio_edit.getText().toString());
        outState.putString("address",address_edit.getText().toString());
        outState.putString("zip",zip_edit.getText().toString());
        outState.putString("zone",zone_edit.getText().toString());
        outState.putString("username",username_edit.getText().toString());

        super.onSaveInstanceState(outState);

    }
}
