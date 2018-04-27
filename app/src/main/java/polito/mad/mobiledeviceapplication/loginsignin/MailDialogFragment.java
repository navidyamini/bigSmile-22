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
        email_layout = (TextInputLayout) v.findViewById(R.id.publisher_layout);
        password_layout = (TextInputLayout) v.findViewById(R.id.password_lay);

        if (getTag().equals("MailDialog"))
            confirm_button.setText("Log in");
        else
            confirm_button.setText("Sign up");

        confirm_button.requestFocus();

        //typology = b.getString("typology");


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


   /*     getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

*/

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (email.getText().toString().equals(""))
                    email_layout.setError(getString(R.string.cant_empty));

                else if (password.getText().toString().equals(""))
                    password_layout.setError(getString(R.string.cant_empty));

                else {

                    email_layout.setErrorEnabled(false);
                    password_layout.setErrorEnabled(false);
                    Activity a = getActivity();
                    if (a instanceof LoginFragment.Frag1Observer && getTag().equals("MailDialog")) {

                        LoginFragment.Frag1Observer observer = (LoginFragment.Frag1Observer) a;
                        Intent intent = new Intent(Constants.SIGNIN_EMAIL);
                        intent.putExtra("email", email.getText().toString());
                        intent.putExtra("password", password.getText().toString());
                        observer.notifyActionLogin(intent);

                    } else if (a instanceof SignupFragment.Frag2Observer && getTag().equals("MailDialogSignup")){

                        SignupFragment.Frag2Observer observer = (SignupFragment.Frag2Observer) a;
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
