package polito.mad.mobiledeviceapplication.loginsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 22/04/2018.
 */

public class PhoneDialogFragment extends android.support.v4.app.DialogFragment {

    private TextInputEditText phone;
    private TextInputLayout phone_layout;
    //private TextInputEditText code;
    private Button confirm_button;
    private ProgressBar loading_progress;
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.phone_fragment, vg, false);
        confirm_button=(Button)v.findViewById(R.id.phone_button);
        phone = (TextInputEditText) v.findViewById(R.id.phone);
        phone_layout = (TextInputLayout) v.findViewById(R.id.edit_year_layout);
        loading_progress = (ProgressBar) v.findViewById(R.id.loadingProgressBar);
        //code = (TextInputEditText) v.findViewById(R.id.code);


        confirm_button.requestFocus();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

/*
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
*/

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.util.Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
                    phone_layout.setErrorEnabled(false);
                    Activity a = getActivity();
                    if (a instanceof IntroLoginFragment.Frag1Observer) {
                        IntroLoginFragment.Frag1Observer observer = (IntroLoginFragment.Frag1Observer) a;
                        Intent intent = new Intent(Constants.SIGNIN_PHONE);
                        intent.putExtra("phone", phone.getText().toString());
                        //intent.putExtra("password",password.getText().toString());
                        observer.notifyActionLogin(intent);
                    }
                } else {

                    phone_layout.setError("Phone number must be formatted with global format. ([+][country code][subscriber number including area code])");

                }

                //dismiss();
            }
        });

    }
}
