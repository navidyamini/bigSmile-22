package polito.mad.mobiledeviceapplication.loginsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 22/04/2018.
 */

public class MailDialogFragment extends DialogFragment {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputLayout email_layout, password_layout;
    private Button confirm_button;
    private String typology;
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = li.inflate(R.layout.mail_fragment, vg, false);
        confirm_button=(Button)v.findViewById(R.id.button);
        email = (TextInputEditText) v.findViewById(R.id.email);
        password = (TextInputEditText) v.findViewById(R.id.password);
        email_layout = (TextInputLayout) v.findViewById(R.id.email_layout);
        password_layout = (TextInputLayout) v.findViewById(R.id.password_lay);

        confirm_button.requestFocus();

        //typology = b.getString("typology");


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (email.getText().toString().equals(""))
                    email_layout.setError("This field cannot be empty");

                else if (password.getText().toString().equals(""))
                    password_layout.setError("This field cannot be empty");

                else {

                    email_layout.setErrorEnabled(false);
                    password_layout.setErrorEnabled(false);
                    Activity a = getActivity();
                    if (a instanceof IntroLoginFragment.Frag1Observer) {

                        IntroLoginFragment.Frag1Observer observer = (IntroLoginFragment.Frag1Observer) a;
                        Intent intent = new Intent(Constants.SIGNIN_EMAIL);
                        intent.putExtra("email", email.getText().toString());
                        intent.putExtra("password", password.getText().toString());
                        observer.notifyActionLogin(intent);

                    } else if (a instanceof IntroSignupFragment.Frag2Observer){

                        IntroSignupFragment.Frag2Observer observer = (IntroSignupFragment.Frag2Observer) a;
                        Intent intent = new Intent(Constants.SIGNUP_EMAIL);
                        intent.putExtra("email", email.getText().toString());
                        intent.putExtra("password", password.getText().toString());
                        observer.notifyActionSignup(intent);


                    }

                    dismiss();
                }

            }
        });

    }
}
