package polito.mad.mobiledeviceapplication.books;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;

public class EditBookActivity extends AppCompatActivity {

    private TextInputEditText isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags;
    private TextInputLayout isbn_layout, title_layout, author_layout, publisher_layout, edition_year_layout, book_conditions_layout, genre_layout, extra_tags_layout;
    private TextView title_dialog;
    private String image_url;
    private ImageView book_image;

    private FloatingActionButton confirmBook, scanBook, deleteImageBook;
    private RelativeLayout form;
    private TextSwitcher explaination_switcher;

    //add Image
    private FloatingActionButton pickImageButton;
    private static final int CAMERA = 10;
    private static final int GALLERY = 11;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 2;
    private static final int REQUEST_PERMISSION_SETTING = 3;

    private ImageView bookImg;
    private Bitmap imageBitmap;

    private boolean permission_bool;

    public FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        permission_bool = false;
        checkPermissions();

        pickImageButton = (FloatingActionButton) findViewById(R.id.takeBookImage);
        bookImg = (ImageView) findViewById(R.id.bookImage);

        form = (RelativeLayout) findViewById(R.id.form_layout);

        isbn = (TextInputEditText) findViewById(R.id.edit_isbn);
        title = (TextInputEditText) findViewById(R.id.edit_title);
        author = (TextInputEditText) findViewById(R.id.edit_author);
        publisher = (TextInputEditText) findViewById(R.id.edit_publisher);
        book_conditions = (TextInputEditText) findViewById(R.id.edit_book_conditions);
        edition_year = (TextInputEditText) findViewById(R.id.edit_edition_year);
        genre = (TextInputEditText) findViewById(R.id.edit_genre);
        extra_tags = (TextInputEditText) findViewById(R.id.edit_tags);

        isbn_layout = (TextInputLayout) findViewById(R.id.isbn_text_input_layout);
        title_layout = (TextInputLayout) findViewById(R.id.title_text_input_layout);
        author_layout = (TextInputLayout) findViewById(R.id.author_text_input_layout);
        publisher_layout = (TextInputLayout) findViewById(R.id.publisher_text_input_layout);
        book_conditions_layout = (TextInputLayout) findViewById(R.id.book_conditions_text_input_layout);
        edition_year_layout = (TextInputLayout) findViewById(R.id.edit_year_text_input_layout);
        genre_layout = (TextInputLayout) findViewById(R.id.genre_text_input_layout);
        extra_tags_layout = (TextInputLayout) findViewById(R.id.tags_text_input_layout);

        explaination_switcher = (TextSwitcher) findViewById(R.id.explaination_switcher);
        explaination_switcher.setInAnimation(getApplicationContext(),android.R.anim.fade_in);
        explaination_switcher.setOutAnimation(getApplicationContext(),android.R.anim.fade_out);

        title_dialog = (TextView) findViewById(R.id.title_dialog);

        TextView t1 = new TextView(getApplicationContext());
        t1.setTextColor(Color.WHITE);
        //t1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/roboto_light.ttf"));

        TextView t2 = new TextView(getApplicationContext());
        t2.setTextColor(Color.WHITE);
        //t2.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/roboto_light.ttf"));


        explaination_switcher.addView(t1);
        explaination_switcher.addView(t2);

        Animation in = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.slide_out_right);

        explaination_switcher.setInAnimation(in);
        explaination_switcher.setOutAnimation(out);

        explaination_switcher.setCurrentText(getString(R.string.book_insert_hint));

        confirmBook = (FloatingActionButton) findViewById(R.id.confirmBook);
        scanBook = (FloatingActionButton) findViewById(R.id.scan_book_btn);
        deleteImageBook = (FloatingActionButton) findViewById(R.id.delete_image_btn);
        deleteImageBook.setVisibility(View.INVISIBLE);



        retrieveBookInformation();

    }

    private void retrieveBookInformation() {
        mAuth = FirebaseAuth.getInstance();

        System.out.println(getIntent().getStringExtra("book_id"));
        final String userID = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userID).child("books").child(getIntent().getStringExtra("book_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                title.setText(book.title);
                isbn.setText(book.ISBN);
                author.setText(book.author);
                publisher.setText(book.publisher);
                book_conditions.setText(book.book_conditions);
                edition_year.setText(book.edition_year);
                genre.setText(book.genre);
                extra_tags.setText(book.extra_tags);

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReference().child("images").child("books").child(getIntent().getStringExtra("book_id"));

                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        book_image.setImageBitmap(bmp);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors

                        //ASK FIREBASE TO RETRIEVE USER INFO AND BOOK ADDITIONAL INFO

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("aaa", "loadPost:onCancelled", databaseError.toException());
            }
        });




    }
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getApplicationContext(), R.style.CustomDialog);
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

    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
        else
            permission_bool = true;


    }

    public String saveImage(Bitmap myBitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = null;

        if (mAuth==null)
            mAuth = FirebaseAuth.getInstance();

        wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+mAuth.getCurrentUser().getUid());

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, "book.jpg");
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

    public void deleteImage(){

        File wallpaperDirectory = null;

        if (mAuth==null)
            mAuth = FirebaseAuth.getInstance();

        wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+mAuth.getCurrentUser().getUid());



        File fdelete = new File(wallpaperDirectory.getAbsolutePath(),"book.jpg");
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + fdelete.getAbsolutePath());
            } else {
                System.out.println("file not Deleted :" + fdelete.getAbsolutePath());
            }
        }



    }

    private boolean checkISBN(){

        if (isbn.getText().length()==0) {
            isbn_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            isbn_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean checkTitle(){

        if (title.getText().length()==0) {
            title_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            title_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean checkAuthor(){

        if (author.getText().length()==0) {
            author_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            author_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean checkPublisher(){

        if (publisher.getText().length()==0) {
            publisher_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            publisher_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean checkEditionYear(){


        if (edition_year.getText().length()==0) {
            edition_year_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            edition_year_layout.setErrorEnabled(false);
            return true;
        }


    }
    private boolean checkGenre(){

        if (genre.getText().length()==0) {
            genre_layout.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            genre_layout.setErrorEnabled(false);
            return true;
        }

    }


}