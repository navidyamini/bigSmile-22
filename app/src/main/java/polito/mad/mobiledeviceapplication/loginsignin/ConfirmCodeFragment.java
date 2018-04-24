package polito.mad.mobiledeviceapplication.loginsignin;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import polito.mad.mobiledeviceapplication.R;

/**
 * Created by user on 22/04/2018.
 */

public class ConfirmCodeFragment extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.phone_fragment, vg, false);
/*        confirm_button=(Button)v.findViewById(R.id.button);
        phone = (TextInputEditText) v.findViewById(R.id.phone);
        //code = (TextInputEditText) v.findViewById(R.id.code);


        confirm_button.requestFocus();*/
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
