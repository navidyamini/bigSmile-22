package polito.mad.mobiledeviceapplication.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;

/**
 * Created by user on 24/04/2018.
 */

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_search, container, false);

        ((MainActivity)getActivity()).toolbar.setTitle(R.string.search);
        getChildFragmentManager().beginTransaction().replace(R.id.container,new SearchForm()).commit();



        return rootView;
    }
}
