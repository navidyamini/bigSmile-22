package polito.mad.mobiledeviceapplication.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.loginsignin.LoginFragment;
import polito.mad.mobiledeviceapplication.profile.ShowProfileActivity;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 08/05/2018.
 */

public class SearchForm extends Fragment {
    private Button searchButton,clearButton;
    private TextInputEditText author,title,genre,publisher;

    public interface FragSearchObserver {
        void notifySearchRequest(Intent intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_search_form, container, false);

        author = (TextInputEditText) rootView.findViewById(R.id.author);
        title = (TextInputEditText) rootView.findViewById(R.id.title);
        genre = (TextInputEditText) rootView.findViewById(R.id.genre);
        publisher = (TextInputEditText) rootView.findViewById(R.id.publisher);

        searchButton = (Button) rootView.findViewById(R.id.button_search);
        searchButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a=getActivity();
                if (a instanceof SearchForm.FragSearchObserver) {
                    SearchForm.FragSearchObserver observer = (SearchForm.FragSearchObserver) a;
                    Intent intent = new Intent(Constants.SEARCH_RESULT);
                    intent.putExtra("author",author.getText().toString());
                    intent.putExtra("title",title.getText().toString());
                    intent.putExtra("genre",genre.getText().toString());
                    intent.putExtra("publisher",publisher.getText().toString());
                    observer.notifySearchRequest(intent);
                }

            }
        }));


        clearButton = (Button) rootView.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                author.setText("");
                title.setText("");
                genre.setText("");
                publisher.setText("");

                author.requestFocus();
            }
        });

        return rootView;
    }

}
