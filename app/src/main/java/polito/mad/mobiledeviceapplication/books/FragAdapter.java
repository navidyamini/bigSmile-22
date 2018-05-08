package polito.mad.mobiledeviceapplication.books;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import polito.mad.mobiledeviceapplication.R;

/**
 * Created by Asus on 4/24/2018.
 */

public class FragAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    private Context context;
    private String[] tabTitles;


    public FragAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.context = context;
        this.tabCount=tabCount;
        tabTitles = new String[]{context.getResources().getString(R.string.all_books),
                context.getResources().getString(R.string.lent_books),
                context.getResources().getString(R.string.available_books)};

    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0 :
                AllBooks allMyBooksFrag = new AllBooks();
                return allMyBooksFrag;
            case 1 :
                AvailableBooks myAvailableBooksFrag = new AvailableBooks();
                return myAvailableBooksFrag;
            case 2 :
                LentBooks myLentBooks = new LentBooks();
                return myLentBooks;
            default: return null;
        }
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
