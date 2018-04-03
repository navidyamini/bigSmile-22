package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;


public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout name_text_layout,surname_text_layout,email_text_layout,phone_text_layout,bio_text_layout,address_text_layout,zip_text_layout,zone_text_layout;
    private TextInputEditText name_edit,surname_edit,email_edit,phone_edit,bio_edit,address_edit,zip_edit,zone_edit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
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



        name_edit = (TextInputEditText) findViewById(R.id.name_edit_text);
        surname_edit = (TextInputEditText) findViewById(R.id.surname_edit_text);
        email_edit = (TextInputEditText) findViewById(R.id.email_edit_text);
        phone_edit = (TextInputEditText) findViewById(R.id.phone_edit_text);
        bio_edit = (TextInputEditText) findViewById(R.id.bio_edit_text);
        address_edit = (TextInputEditText) findViewById(R.id.address_edit_text);
        zip_edit = (TextInputEditText) findViewById(R.id.zip_edit_text);
        zone_edit = (TextInputEditText) findViewById(R.id.zone_edit_text);



        name_edit.setText(getSharedPreferences("ProfilePref", 0).getString("name", ""));
        surname_edit.setText(getSharedPreferences("ProfilePref", 0).getString("surname", ""));
        email_edit.setText(getSharedPreferences("ProfilePref", 0).getString("email", ""));
        phone_edit.setText(getSharedPreferences("ProfilePref", 0).getString("phone", ""));
        bio_edit.setText(getSharedPreferences("ProfilePref", 0).getString("bio", ""));
        address_edit.setText(getSharedPreferences("ProfilePref", 0).getString("address", ""));
        zip_edit.setText(getSharedPreferences("ProfilePref", 0).getString("ZIP", ""));
        zone_edit.setText(getSharedPreferences("ProfilePref", 0).getString("zone", ""));

        name_edit.setSelection(name_edit.getText().length());
        surname_edit.setSelection(surname_edit.getText().length());
        email_edit.setSelection(email_edit.getText().length());
        phone_edit.setSelection(phone_edit.getText().length());
        bio_edit.setSelection(bio_edit.getText().length());
        address_edit.setSelection(address_edit.getText().length());
        zip_edit.setSelection(zip_edit.getText().length());
        zone_edit.setSelection(zone_edit.getText().length());

        bio_edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        bio_edit.setRawInputType(InputType.TYPE_CLASS_TEXT);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:

                if (name_edit.getText().length()==0)
                    name_text_layout.setError(getString(R.string.not_empty));
                else if (surname_edit.getText().length()==0)
                    surname_text_layout.setError(getString(R.string.not_empty));
                else{
                    name_text_layout.setErrorEnabled(false);
                    surname_text_layout.setErrorEnabled(false);

                    getSharedPreferences("ProfilePref", 0).edit().putString("name", name_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("surname", surname_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("email", email_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("phone", phone_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("bio", bio_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("address", address_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("ZIP", zip_edit.getText().toString()).apply();
                    getSharedPreferences("ProfilePref", 0).edit().putString("zone", zone_edit.getText().toString()).apply();

                    Intent intent = new Intent(this, ShowProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(intent);
                    finish();
                }

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }







}
