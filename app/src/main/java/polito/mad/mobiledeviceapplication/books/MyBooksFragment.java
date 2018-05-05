package polito.mad.mobiledeviceapplication.books;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import polito.mad.mobiledeviceapplication.R;

/**
 * Created by user on 24/04/2018.
 */

public class MyBooksFragment extends Fragment {

    private FloatingActionButton add_book;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_mybooks, container, false);

        add_book = (FloatingActionButton)rootView.findViewById(R.id.addBookBtn);
        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBookDialogFragment addBookDialogFragment = new AddBookDialogFragment();
                addBookDialogFragment.show(getChildFragmentManager(),"AddBookDialog");

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

