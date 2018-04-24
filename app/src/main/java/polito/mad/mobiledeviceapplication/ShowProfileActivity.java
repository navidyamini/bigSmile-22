package polito.mad.mobiledeviceapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShowProfileActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String[]> arrayList;
    private TextView name;

    private FloatingActionButton pickImageButton;
    private static final int CAMERA = 10;
    private static final int GALLERY = 11;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 2;
    private static final int REQUEST_PERMISSION_SETTING = 3;

    private ImageView profileImage;

    private boolean permission_bool = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_show);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
        else
            permission_bool = true;

        name = (TextView) findViewById(R.id.name);
        listView = (ListView) findViewById(R.id.list_profile);
        pickImageButton = (FloatingActionButton) findViewById(R.id.changePictureBtn);
        profileImage = (ImageView) findViewById(R.id.profileImage);


        retrieveProfileImage(profileImage);


        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (permission_bool)
                    showPictureDialog();

            }
        });

        if (!getSharedPreferences("ProfilePref",0).getString("name","").equals("")
                && !getSharedPreferences("ProfilePref",0).getString("surname","").equals(""))
            name.setText(getSharedPreferences("ProfilePref",0).getString("name","") + " " + getSharedPreferences("ProfilePref",0).getString("surname",""));

        arrayList = new ArrayList<>();
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("name", ""), getString(R.string.user_name)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("surname", ""), getString(R.string.user_surname)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("email", ""), getString(R.string.user_email)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("phone", ""), getString(R.string.user_PhoneNumber)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("bio", ""), getString(R.string.user_ShortBio)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("address", ""), getString(R.string.user_address)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("ZIP", ""), getString(R.string.user_zipCode)});
        arrayList.add(new String[]{getSharedPreferences("ProfilePref", 0).getString("zone", ""), getString(R.string.user_zone)});

        listView.setAdapter(new CustomAdapterShow(getApplicationContext(), arrayList));




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this, R.style.CustomDialog);
        pictureDialog.setTitle(getString(R.string.select_action));
        String[] pictureDialogItems = {
                getString(R.string.gallery),
                getString(R.string.camera)};

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, "profile.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_PERMISSION_SETTING){


                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowProfileActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

                } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);

                }


        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(this, getString(R.string.save_image), Toast.LENGTH_SHORT).show();
                    profileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            if (data!=null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(thumbnail);
                saveImage(thumbnail);
                Toast.makeText(this, getString(R.string.save_image), Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    protected void onResume() {



        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {



        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            saveImage(bitmap);
        }
        super.onSaveInstanceState(outState);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);


                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialog);
                        builder.setTitle(getString(R.string.permission));
                        builder.setMessage(getString(R.string.permission_message));
                        builder.setCancelable(true);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ShowProfileActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        });

                        builder.create().show();

                    } else {

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);


                    }


                }

                break;
            }


            case MY_PERMISSIONS_REQUEST_WRITE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permission_bool = true;


                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialog);
                        builder.setTitle(getString(R.string.permission));
                        builder.setMessage(getString(R.string.permission_message));
                        builder.setCancelable(true);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    ActivityCompat.requestPermissions(ShowProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
                            }
                        });

                        builder.create().show();

                    } else {

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);


                    }


                }


                break;

            }

        }
    }

    private void retrieveProfileImage(ImageView imageView){

        try {
            String photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/profile.jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            if (bitmap!=null)
                imageView.setImageBitmap(bitmap);
        }catch (Exception e){}


    }


}
