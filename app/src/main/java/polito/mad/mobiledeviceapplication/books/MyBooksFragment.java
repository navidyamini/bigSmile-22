package polito.mad.mobiledeviceapplication.books;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;

/**
 * Created by user on 24/04/2018.
 */

public class MyBooksFragment extends Fragment {

    private FloatingActionButton add_book;
    private ViewPager pager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_mybooks, container, false);

        pager = (ViewPager) rootView.findViewById(R.id.container);
        add_book = (FloatingActionButton)rootView.findViewById(R.id.addBookBtn);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(new FragAdapter(getChildFragmentManager(),3,getContext()));

        ((MainActivity)getActivity()).toolbar.setTitle(R.string.my_books);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBookDialogFragment addBookDialogFragment = new AddBookDialogFragment();
                addBookDialogFragment.show(getChildFragmentManager(),"AddBookDialog");

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

