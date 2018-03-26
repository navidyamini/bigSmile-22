package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowProfileActivity extends AppCompatActivity {

    private TextView nameText,emailText,phoneText,bioText, cityText, zipCodeText, zoneText;

    private static int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        nameText = (TextView)findViewById(R.id.name_view);
        emailText = (TextView)findViewById(R.id.email_view);
        phoneText = (TextView)findViewById(R.id.phone_view);
        bioText = (TextView)findViewById(R.id.bio_view);
        cityText = (TextView)findViewById(R.id.city_view);
        zipCodeText = (TextView)findViewById(R.id.zip_view);
        zoneText = (TextView)findViewById(R.id.zone_view);

        nameText.setText(getIntent().getExtras().getString("nameValue"));
        emailText.setText(getIntent().getExtras().getString("emailValue"));
        phoneText.setText(getIntent().getExtras().getString("phoneValue"));
        bioText.setText(getIntent().getExtras().getString("bioValue"));
        cityText.setText(getIntent().getExtras().getString("cityValue"));
        zipCodeText.setText(getIntent().getExtras().getString("zipCodeValue"));
        zoneText.setText(getIntent().getExtras().getString("zoneValue"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
