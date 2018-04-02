package polito.mad.mobiledeviceapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String[]> arrayList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_edit);
        setSupportActionBar(myToolbar);

        listView = (ListView) findViewById(R.id.list_edit);


        arrayList = new ArrayList<>();
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("name", ""), "Name"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("surname", ""), "Surname"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("email", ""), "Email"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("phone", ""), "Telephone number"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("bio", ""), "Bio"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("address", ""), "Address"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("ZIP", ""), "ZIP"});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("zone", ""), "Zone"});

        listView.setAdapter(new CustomAdapterEdit(getApplicationContext(), arrayList));


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

                Intent intent = new Intent(this, ShowProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                System.out.println(listView.getChildCount());
                if(listView.getChildAt(0)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("name",((TextInputEditText)listView.getChildAt(0).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(1)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("surname",((TextInputEditText)listView.getChildAt(1).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(2)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("email",((TextInputEditText)listView.getChildAt(2).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(3)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("phone",((TextInputEditText)listView.getChildAt(3).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(4)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("bio",((TextInputEditText)listView.getChildAt(4).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(5)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("address",((TextInputEditText)listView.getChildAt(5).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(6)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("ZIP",((TextInputEditText)listView.getChildAt(6).findViewById(R.id.edit_text)).getText().toString()).apply();
                if(listView.getChildAt(7)!=null)
                    getSharedPreferences("ProfilePref",0).edit().putString("zone",((TextInputEditText)listView.getChildAt(7).findViewById(R.id.edit_text)).getText().toString()).apply();

                this.startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

/*
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences("ProfilePref",0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", ((TextInputEditText)listView.getChildAt(0).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("email", ((TextInputEditText)listView.getChildAt(1).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("phone", ((TextInputEditText)listView.getChildAt(2).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("bio",((TextInputEditText)listView.getChildAt(3).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("address", ((TextInputEditText)listView.getChildAt(4).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("ZIP", ((TextInputEditText)listView.getChildAt(5).findViewById(R.id.edit_text)).getText().toString());
        editor.putString("zone", ((TextInputEditText)listView.getChildAt(6).findViewById(R.id.edit_text)).getText().toString());
        editor.commit();

    }
*/





}
