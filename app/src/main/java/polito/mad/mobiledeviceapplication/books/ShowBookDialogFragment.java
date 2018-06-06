package polito.mad.mobiledeviceapplication.books;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.search.SearchForm;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 11/05/2018.
 */

public class ShowBookDialogFragment extends DialogFragment {

    private TextView title,author,publisher,edition_year,genre,book_conditions,extra_tags,isbn;
    private TextView name_surname;
    private ImageView cover, book_conditions_image;
    private Button contact_button;
    private Button borrow_request;

    public interface FragContactObserver {
        void notifyContactRequest(Intent intent);
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_book, container, false);



        title = (TextView) v.findViewById(R.id.title);
        author = (TextView) v.findViewById(R.id.author);
        publisher = (TextView) v.findViewById(R.id.publisher);
        edition_year = (TextView) v.findViewById(R.id.edition_year);
        genre = (TextView) v.findViewById(R.id.genre);
        book_conditions = (TextView) v.findViewById(R.id.book_conditions);
        extra_tags = (TextView) v.findViewById(R.id.extra_tags);
        isbn = (TextView) v.findViewById(R.id.isbn);

        cover = (ImageView) v.findViewById(R.id.cover);
        book_conditions_image = (ImageView) v.findViewById(R.id.book_condition_image);
        name_surname = (TextView) v.findViewById(R.id.name_surname);

        contact_button = (Button) v.findViewById(R.id.contact_button);

        borrow_request = (Button) v.findViewById(R.id.borrow_button);



        if (getArguments()!=null){

            title.setText(getArguments().getString("title"));
            author.setText("Autore: " + getArguments().getString("author"));
            publisher.setText("Casa editrice: " + getArguments().getString("publisher"));
            edition_year.setText("Anno di pubblicazione: " + getArguments().getString("edition_year"));
            genre.setText("Genere: " + getArguments().getString("genre"));
            book_conditions.setText("Condizioni: " + getArguments().getString("book_conditions"));
            extra_tags.setText("Informazioni extra: " +getArguments().getString("extra_tags"));
            isbn.setText("ISBN: " +getArguments().getString("isbn"));

            name_surname.setText("Proprietario: " + getArguments().getString("name") + " " + getArguments().getString("surname"));

            ImageRequest request = new ImageRequest(getArguments().getString("image_url"),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            cover.setImageBitmap(bitmap);


                        }
                    },0,0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            cover.setImageResource(R.drawable.blank);

                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(getContext()).add(request);


            if (getArguments().getByteArray("book_conditions_image")!=null) {

                byte[] bytes = getArguments().getByteArray("book_conditions_image");
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                book_conditions_image.setImageBitmap(bmp);
                book_conditions_image.setVisibility(View.VISIBLE);

            }

        }

        contact_button.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a=getActivity();
                if (a instanceof ShowBookDialogFragment.FragContactObserver) {
                    ShowBookDialogFragment.FragContactObserver observer = (ShowBookDialogFragment.FragContactObserver) a;
                    Intent intent = new Intent(Constants.Contact_Request);
                    intent.putExtra("user_id_r",getArguments().getString("user_id",""));
                    intent.putExtra("username_r",getArguments().getString("name"));
                    observer.notifyContactRequest(intent);
                }

            }
        }));

        borrow_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
                View v = inflater.inflate(R.layout.borrow_request,container,false);
                final EditText startDate = v.findViewById(R.id.startDateEditText);
                final EditText endDate = v.findViewById(R.id.endDateEditText);

                startDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog picker;
                        //DatePickerDialog date = new DatePickerDialog(getContext());
                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        // date picker dialog
                        picker = new DatePickerDialog(getContext(), R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        dismiss();
                                    }
                                }, year, month, day);
                        picker.show();

                    }
                });
                endDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog picker1;
                        //DatePickerDialog date = new DatePickerDialog(getContext());
                        final Calendar cldr = Calendar.getInstance();

                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        System.out.println(getContext());
                        // date picker dialog
                        picker1 = new DatePickerDialog(view.getContext(), R.style.DialogTheme,

                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        endDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        dismiss();
                                    }
                                }, year, month, day);
                        picker1.show();

                    }
                });

                mbuilder.setView(v);
                mbuilder.setCancelable(false);
                mbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //TODO make request
                    }
                });
                mbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mbuilder.create().show();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


    }
}
