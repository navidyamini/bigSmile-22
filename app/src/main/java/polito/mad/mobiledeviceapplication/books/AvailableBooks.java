package polito.mad.mobiledeviceapplication.books;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import polito.mad.mobiledeviceapplication.R;

/**
 * Created by Asus on 4/24/2018.
 */

public class AvailableBooks extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_books,container,false);
        return view;
    }
}
