package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameText,emailText,phoneText,bioText, cityText, zipCodeText, zoneText;

    private static int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        nameText = (EditText)findViewById(R.id.name_edit);
        emailText = (EditText)findViewById(R.id.email_edit);
        phoneText = (EditText)findViewById(R.id.phone_edit);
        bioText = (EditText)findViewById(R.id.bio_edit);
        cityText = (EditText)findViewById(R.id.city_edit);
        zipCodeText = (EditText)findViewById(R.id.zip_edit);
        zoneText = (EditText)findViewById(R.id.zone_edit);

        SharedPreferences settings = getSharedPreferences("ProfilePref",0);

        nameText.setText(settings.getString("nameValue",""));
        emailText.setText(settings.getString("emailValue",""));
        phoneText.setText(settings.getString("phoneValue",""));
        bioText.setText(settings.getString("bioValue",""));
        cityText.setText(settings.getString("cityValue",""));
        zipCodeText.setText(settings.getString("zipCodeValue",""));
        zoneText.setText(settings.getString("zoneValue",""));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                Intent intent = new Intent(this, ShowProfileActivity.class);
                intent.putExtra("nameValue", nameText.getText().toString());
                intent.putExtra("nameValue", nameText.getText().toString());
                intent.putExtra("emailValue", emailText.getText().toString());
                intent.putExtra("phoneValue", phoneText.getText().toString());
                intent.putExtra("bioValue", bioText.getText().toString());
                intent.putExtra("cityValue", cityText.getText().toString());
                intent.putExtra("zipCodeValue", zipCodeText.getText().toString());
                intent.putExtra("zoneValue", zoneText.getText().toString());
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences("ProfilePref",0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("nameValue", nameText.getText().toString());
        editor.putString("emailValue", emailText.getText().toString());
        editor.putString("phoneValue", phoneText.getText().toString());
        editor.putString("bioValue", bioText.getText().toString());
        editor.putString("cityValue", cityText.getText().toString());
        editor.putString("zipCodeValue", zipCodeText.getText().toString());
        editor.putString("zoneValue", zoneText.getText().toString());
        editor.commit();

    }
}
