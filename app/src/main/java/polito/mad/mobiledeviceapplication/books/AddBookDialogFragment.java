package polito.mad.mobiledeviceapplication.books;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 24/04/2018.
 */

public class AddBookDialogFragment extends DialogFragment{


    private TextInputEditText isbn, title, author, publisher, edition_year, book_conditions, genre, extra_tags;
    private TextInputLayout isbn_layout, title_layout, author_layout, publisher_layout, edition_year_layout, book_conditions_layout, genre_layout, extra_tags_layout;

    public interface FragBookObserver {
        void notifyActionBook(Intent intent);
    }

    private FloatingActionButton confirmBook, scanBook;
    private RelativeLayout form;
    private TextSwitcher explaination_switcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_book, container, false);

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


        confirmBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFields()){
                    Activity a=getActivity();
                    if (a instanceof AddBookDialogFragment.FragBookObserver) {
                        AddBookDialogFragment.FragBookObserver observer = (AddBookDialogFragment.FragBookObserver) a;
                        Intent intent = new Intent(Constants.NEW_BOOK);
                        intent.putExtra("isbn", isbn.getText().toString());
                        intent.putExtra("title", title.getText().toString());
                        intent.putExtra("author", author.getText().toString());
                        intent.putExtra("publisher", publisher.getText().toString());
                        intent.putExtra("book_conditions", book_conditions.getText().toString());
                        intent.putExtra("edition_year", edition_year.getText().toString());
                        intent.putExtra("genre", genre.getText().toString());
                        intent.putExtra("extra_tags", extra_tags.getText().toString());

                        observer.notifyActionBook(intent);

                    }
                }


            }
        });

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

        author.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkFields();
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

                checkFields();
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

                checkFields();
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

                checkFields();
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

                checkFields();
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

                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public void setFields(String isbn, String title, String author, String publisher, String book_conditions, String edition_year, String genre, String extra_tags){

        this.isbn.setText(isbn);
        this.title.setText(title);
        this.author.setText(author);
        this.publisher.setText(publisher);
        this.book_conditions.setText(book_conditions);
        this.edition_year.setText(edition_year);
        this.genre.setText(genre);
        this.extra_tags.setText(extra_tags);

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
        }
    }

    @Override
    public void onDestroyView() {

        explaination_switcher.removeAllViews();
        super.onDestroyView();
    }


    private boolean checkFields(){

        boolean isValid = true;
        if (isbn.getText().length()==0) {
            isbn_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            isbn_layout.setErrorEnabled(false);

        if (title.getText().length()==0) {
            title_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            title_layout.setErrorEnabled(false);

        if (author.getText().length()==0) {
            author_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            author_layout.setErrorEnabled(false);

        if (publisher.getText().length()==0) {
            publisher_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            publisher_layout.setErrorEnabled(false);

        if (edition_year.getText().length()==0) {
            edition_year_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            edition_year_layout.setErrorEnabled(false);

        if (genre.getText().length()==0) {
            genre_layout.setError(getString(R.string.not_empty));
            isValid = false;
        }
        else
            genre_layout.setErrorEnabled(false);

        return isValid;
    }
}
