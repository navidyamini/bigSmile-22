package polito.mad.mobiledeviceapplication.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.profile.ShowProfileActivity;

/**
 * Created by user on 24/04/2018.
 */

public class MyBooksFragment extends Fragment {

    private FloatingActionButton add_book,edit_book;
    private ViewPager pager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_mybooks, container, false);

        pager = (ViewPager) rootView.findViewById(R.id.container);
        add_book = (FloatingActionButton) rootView.findViewById(R.id.addBookBtn);
        edit_book = (FloatingActionButton) rootView.findViewById(R.id.editBookBtn);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(new FragAdapter(getChildFragmentManager(), 3, getContext()));

        ((MainActivity) getActivity()).toolbar.setTitle(R.string.my_books);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBookDialogFragment addBookDialogFragment = new AddBookDialogFragment();
                addBookDialogFragment.show(getChildFragmentManager(), "AddBookDialog");

            }
        });

        edit_book.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(((FragAdapter)pager.getAdapter()).getFragment(0) != null){
                    Intent intent = new Intent(getContext(), EditBookActivity.class);
                    AllBooks fragmentEdit =(AllBooks)((FragAdapter)pager.getAdapter()).getFragment(0);
                    intent.putExtra("book_id",fragmentEdit.currentBookId);
                    startActivity(intent);

                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class FragAdapter extends FragmentStatePagerAdapter {

        int tabCount;
        private Context context;
        private String[] tabTitles;
        private ArrayList<Fragment> fragments;


        public FragAdapter(FragmentManager fm, int tabCount, Context context) {
            super(fm);
            this.context = context;
            this.tabCount = tabCount;
            tabTitles = new String[]{context.getResources().getString(R.string.all_books),
                    context.getResources().getString(R.string.lent_books),
                    context.getResources().getString(R.string.available_books)};
            this.fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    AllBooks allMyBooksFrag = new AllBooks();
                    fragments.add(0,allMyBooksFrag);
                    return allMyBooksFrag;

                case 1:
                    LentBooks myLentBooks = new LentBooks();
                    fragments.add(1,myLentBooks);
                    return myLentBooks;

                case 2:
                    AvailableBooks myAvailableBooksFrag = new AvailableBooks();
                    fragments.add(2,myAvailableBooksFrag);
                    return myAvailableBooksFrag;

                default:
                    return null;
            }
        }
        public Fragment getFragment(int position){
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];

        }
    }
}

