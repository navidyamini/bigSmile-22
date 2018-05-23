package polito.mad.mobiledeviceapplication.books;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    public interface FragContactObserver {
        void notifyContactRequest(Intent intent);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
                    intent.putExtra("user_id",getArguments().getString("user_id",""));
                    observer.notifyContactRequest(intent);
                }

            }
        }));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


    }
}
