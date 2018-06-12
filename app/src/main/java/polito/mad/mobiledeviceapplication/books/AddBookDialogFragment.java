package polito.mad.mobiledeviceapplication.books;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Book;
import polito.mad.mobiledeviceapplication.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 24/04/2018.
 */

public class AddBookDialogFragment extends DialogFragment{


    private TextInputEditText isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags;
    private TextInputLayout isbn_layout, title_layout, author_layout, publisher_layout, edition_year_layout, book_conditions_layout, genre_layout, extra_tags_layout;
    private TextView title_dialog;
    private String image_url;
    private ImageView book_image;

    public interface FragBookObserver {
        void notifyActionBook(Intent intent);
        void retrieveBook(Intent intent);
    }

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

    private Bitmap imageBitmap;

    private boolean permission_bool;

///////////////////////////////////////////////////////////////
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_book, container, false);

        permission_bool = false;
        checkPermissions();

        pickImageButton = (FloatingActionButton) v.findViewById(R.id.takeBookImage);
        book_image = (ImageView) v.findViewById(R.id.bookImage);


/////////////////////////////////////////////////////////////

        form = (RelativeLayout) v.findViewById(R.id.form_layout);

        isbn = (TextInputEditText) v.findViewById(R.id.isbn);
        title = (TextInputEditText) v.findViewById(R.id.title);
        author = (TextInputEditText) v.findViewById(R.id.author);
        publisher = (TextInputEditText) v.findViewById(R.id.publisher);
        book_conditions = (TextInputEditText) v.findViewById(R.id.book_conditions);
        edition_year = (TextInputEditText) v.findViewById(R.id.edition_year);
        genre = (TextInputEditText) v.findViewById(R.id.genre);
        extra_tags = (TextInputEditText) v.findViewById(R.id.tags);

        isbn_layout = (TextInputLayout) v.findViewById(R.id.isbn_text_input_layout);
        title_layout = (TextInputLayout) v.findViewById(R.id.title_text_input_layout);
        author_layout = (TextInputLayout) v.findViewById(R.id.author_text_input_layout);
        publisher_layout = (TextInputLayout) v.findViewById(R.id.publisher_text_input_layout);
        book_conditions_layout = (TextInputLayout) v.findViewById(R.id.book_conditions_text_input_layout);
        edition_year_layout = (TextInputLayout) v.findViewById(R.id.edit_year_text_input_layout);
        genre_layout = (TextInputLayout) v.findViewById(R.id.genre_text_input_layout);
        extra_tags_layout = (TextInputLayout) v.findViewById(R.id.tags_text_input_layout);

        explaination_switcher = (TextSwitcher) v.findViewById(R.id.explaination_switcher);
        explaination_switcher.setInAnimation(getContext(),android.R.anim.fade_in);
        explaination_switcher.setOutAnimation(getContext(),android.R.anim.fade_out);

        title_dialog = (TextView) v.findViewById(R.id.title_dialog);

        TextView t1 = new TextView(getContext());
        t1.setTextColor(Color.WHITE);
        //t1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/roboto_light.ttf"));

        TextView t2 = new TextView(getContext());
        t2.setTextColor(Color.WHITE);
        //t2.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/roboto_light.ttf"));


        explaination_switcher.addView(t1);
        explaination_switcher.addView(t2);

        Animation in = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_out_right);

        explaination_switcher.setInAnimation(in);
        explaination_switcher.setOutAnimation(out);

        explaination_switcher.setCurrentText(getString(R.string.book_insert_hint));

        confirmBook = (FloatingActionButton) v.findViewById(R.id.confirmBook);
        scanBook = (FloatingActionButton) v.findViewById(R.id.scan_book_btn);
        deleteImageBook = (FloatingActionButton) v.findViewById(R.id.delete_image_btn);
        deleteImageBook.setVisibility(View.INVISIBLE);





        return v;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this.getActivity(), R.style.CustomDialog);
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
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = null;

        if (((MainActivity)getActivity()).mAuth==null)
            ((MainActivity)getActivity()).mAuth = FirebaseAuth.getInstance();

        if (((MainActivity)getActivity()).mAuth.getCurrentUser()!=null)
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+((MainActivity)getActivity()).mAuth.getCurrentUser().getUid());
        else
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+getActivity().getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("UID",""));

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, "book.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this.getActivity(),
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_PERMISSION_SETTING){


            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

            } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);

            }


        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), contentURI);
                    String path = saveImage(imageBitmap);
                    Toast.makeText(this.getActivity(), getString(R.string.save_image), Toast.LENGTH_SHORT).show();
                    book_image.setImageBitmap(imageBitmap);
                    explaination_switcher.setVisibility(View.INVISIBLE);
                    title_dialog.setVisibility(View.INVISIBLE);
                    deleteImageBook.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this.getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            if (data!=null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                book_image.setImageBitmap(imageBitmap);
                explaination_switcher.setVisibility(View.INVISIBLE);
                title_dialog.setVisibility(View.INVISIBLE);
                deleteImageBook.setVisibility(View.VISIBLE);
                saveImage(imageBitmap);
                Toast.makeText(this.getActivity(), getString(R.string.save_image), Toast.LENGTH_SHORT).show();
            }
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);


                } else {

                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity(),R.style.CustomDialog);
                        builder.setTitle(getString(R.string.permission));
                        builder.setMessage(getString(R.string.permission_message));
                        builder.setCancelable(true);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        });

                        builder.create().show();

                    } else {

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
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

                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity(),R.style.CustomDialog);
                        builder.setTitle(getString(R.string.permission));
                        builder.setMessage(getString(R.string.permission_message));
                        builder.setCancelable(true);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
                            }
                        });

                        builder.create().show();

                    } else {

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);


                    }


                }


                break;

            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        if (getArguments()!=null) {
            if (getArguments().getBoolean("edit")) {

                title_dialog.setText(getString(R.string.modify_book_txt));
                explaination_switcher.setVisibility(View.INVISIBLE);

                Activity a = getActivity();
                if (a instanceof AddBookDialogFragment.FragBookObserver) {
                    AddBookDialogFragment.FragBookObserver observer = (AddBookDialogFragment.FragBookObserver) a;
                    Intent intent = new Intent(Constants.RETRIEVE_BOOK);
                    intent.putExtra("book_id", getArguments().getString("book_id"));
                    observer.retrieveBook(intent);
                }
            }
        }


        if (getArguments()!=null) {
            if (!getArguments().getBoolean("edit")) {

                confirmBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isISBNValid = checkISBN();
                        boolean isAuthorValid = checkAuthor();
                        boolean isEditionYearValid = checkEditionYear();
                        boolean isGenreValid = checkGenre();
                        boolean isPublisherValid = checkPublisher();
                        boolean isTitleValid = checkTitle();

                        if (isISBNValid && isAuthorValid && isEditionYearValid && isGenreValid && isPublisherValid && isTitleValid) {
                            Activity a = getActivity();
                            if (a instanceof AddBookDialogFragment.FragBookObserver) {
                                AddBookDialogFragment.FragBookObserver observer = (AddBookDialogFragment.FragBookObserver) a;
                                Intent intent = new Intent(Constants.NEW_BOOK);
                                intent.putExtra("ISBN", isbn.getText().toString());
                                intent.putExtra("title", title.getText().toString());
                                intent.putExtra("author", author.getText().toString());
                                intent.putExtra("publisher", publisher.getText().toString());
                                intent.putExtra("book_conditions", book_conditions.getText().toString());
                                intent.putExtra("edition_year", edition_year.getText().toString());
                                intent.putExtra("genre", genre.getText().toString());
                                intent.putExtra("extra_tags", extra_tags.getText().toString());
                                intent.putExtra("image_url", image_url);

                                if (imageBitmap != null)
                                    intent.putExtra("book_bitmap", imageBitmap);

                                observer.notifyActionBook(intent);

                            }
                        }


                    }
                });
            }
        } else {

            confirmBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isISBNValid = checkISBN();
                    boolean isAuthorValid = checkAuthor();
                    boolean isEditionYearValid = checkEditionYear();
                    boolean isGenreValid = checkGenre();
                    boolean isPublisherValid = checkPublisher();
                    boolean isTitleValid = checkTitle();

                    if (isISBNValid && isAuthorValid && isEditionYearValid && isGenreValid && isPublisherValid && isTitleValid) {
                        Activity a = getActivity();
                        if (a instanceof AddBookDialogFragment.FragBookObserver) {
                            AddBookDialogFragment.FragBookObserver observer = (AddBookDialogFragment.FragBookObserver) a;
                            Intent intent = new Intent(Constants.EDIT_BOOK);
                            intent.putExtra("ISBN", isbn.getText().toString());
                            intent.putExtra("title", title.getText().toString());
                            intent.putExtra("author", author.getText().toString());
                            intent.putExtra("publisher", publisher.getText().toString());
                            intent.putExtra("book_conditions", book_conditions.getText().toString());
                            intent.putExtra("edition_year", edition_year.getText().toString());
                            intent.putExtra("genre", genre.getText().toString());
                            intent.putExtra("extra_tags", extra_tags.getText().toString());
                            intent.putExtra("image_url", image_url);

                            if (imageBitmap != null)
                                intent.putExtra("book_bitmap", imageBitmap);

                            observer.notifyActionBook(intent);


                        }
                    }

                }
            });
        }


        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a=getActivity();
                if (a instanceof AddBookDialogFragment.FragBookObserver) {
                    AddBookDialogFragment.FragBookObserver observer = (AddBookDialogFragment.FragBookObserver) a;
                    Intent intent = new Intent(Constants.SCAN_BOOK);

                    observer.notifyActionBook(intent);
                }
            }
        });

        deleteImageBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteImage();
                book_image.setImageDrawable(getResources().getDrawable(R.drawable.header_add_book));
                explaination_switcher.setVisibility(View.VISIBLE);
                title_dialog.setVisibility(View.VISIBLE);
                deleteImageBook.setVisibility(View.INVISIBLE);
            }
        });

        author.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkAuthor();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        publisher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkPublisher();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edition_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkEditionYear();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkTitle();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        genre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkGenre();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        isbn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkISBN();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (permission_bool)
                    showPictureDialog();

            }
        });




    }

    private void retrieveBookImage(ImageView imageView) {
        try {
            String photoPath = "";
            photoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+((MainActivity)getActivity()).mAuth.getCurrentUser().getUid()+"/book.jpg";

            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            if (bitmap!=null) {
                imageView.setImageBitmap(bitmap);
                explaination_switcher.setVisibility(View.INVISIBLE);
                title_dialog.setVisibility(View.INVISIBLE);
                deleteImageBook.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){}
    }

    public void setFields(String isbn, String title, String author, String publisher, String book_conditions, String edition_year, String genre, String extra_tags, String image_url){

        this.isbn.setText(isbn);
        this.title.setText(title);
        this.author.setText(author);
        this.publisher.setText(publisher);
        this.book_conditions.setText(book_conditions);
        this.edition_year.setText(edition_year);
        this.genre.setText(genre);
        this.extra_tags.setText(extra_tags);

        this.image_url= image_url;

    }

    public void setFormEnabled(boolean enabled){

        //this.overlay.setVisibility(visibility);
        if (enabled) {
            form.setEnabled(true);
            this.isbn.setEnabled(true);
            this.title.setEnabled(true);
            this.author.setEnabled(true);
            this.publisher.setEnabled(true);
            this.book_conditions.setEnabled(true);
            this.edition_year.setEnabled(true);
            this.genre.setEnabled(true);
            this.extra_tags.setEnabled(true);

            this.confirmBook.setEnabled(true);
            this.scanBook.setEnabled(true);
            this.pickImageButton.setEnabled(true);

        }
        else {
            form.setEnabled(false);
            this.isbn.setEnabled(false);
            this.title.setEnabled(false);
            this.author.setEnabled(false);
            this.publisher.setEnabled(false);
            this.book_conditions.setEnabled(false);
            this.edition_year.setEnabled(false);
            this.genre.setEnabled(false);
            this.extra_tags.setEnabled(false);

            this.confirmBook.setEnabled(false);
            this.scanBook.setEnabled(false);
            this.pickImageButton.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {

        explaination_switcher.removeAllViews();
        deleteImage();
        super.onDestroyView();
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

    public void deleteImage(){

        File wallpaperDirectory = null;

        if (((MainActivity)getActivity()).mAuth==null)
            ((MainActivity)getActivity()).mAuth = FirebaseAuth.getInstance();

        wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SMILE/pictures/"+((MainActivity)getActivity()).mAuth.getCurrentUser().getUid());



        File fdelete = new File(wallpaperDirectory.getAbsolutePath(),"book.jpg");
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + fdelete.getAbsolutePath());
            } else {
                System.out.println("file not Deleted :" + fdelete.getAbsolutePath());
            }
        }



    }

    private void checkPermissions(){


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
        else
            permission_bool = true;


    }

    public void retrieveBookInformation(Book book, Bitmap image) {


        title.setText(book.title);
        isbn.setText(book.ISBN);
        author.setText(book.author);
        publisher.setText(book.publisher);
        book_conditions.setText(book.book_conditions);
        edition_year.setText(book.edition_year);
        genre.setText(book.genre);
        extra_tags.setText(book.extra_tags);

        if (image!=null)
            book_image.setImageBitmap(image);












    }


}
