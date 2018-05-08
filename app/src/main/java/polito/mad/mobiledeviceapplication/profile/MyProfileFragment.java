package polito.mad.mobiledeviceapplication.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.profile.ShowProfileActivity;

/**
 * Created by user on 24/04/2018.
 */

public class MyProfileFragment extends Fragment {

    private Button access_profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_profile, container, false);

        access_profile = (Button) rootView.findViewById(R.id.enterProfileBtn);
        access_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(),ShowProfileActivity.class);
                startActivity(i);

            }
        });
        ((MainActivity)getActivity()).toolbar.setTitle(R.string.my_profile);

        return rootView;    }
}
