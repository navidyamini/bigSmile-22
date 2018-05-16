package polito.mad.mobiledeviceapplication.loginsignin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 22/04/2018.
 */

public class LoginFragment extends Fragment {

    private EditText username, password;
    private Button access_btn, mail_signin, phone_signin, mail_signup;
    private TextView signup_txt;

    interface Frag1Observer {
        void notifyActionLogin(Intent intent);
        void notifyActionSignup(Intent intent);

    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_intro_login, container, false);

        username = (EditText) rootView.findViewById(R.id.username);
        password = (EditText) rootView.findViewById(R.id.password);

        access_btn = (Button) rootView.findViewById(R.id.accessButton);
        mail_signin = (Button) rootView.findViewById(R.id.EmailAccessBtn);
        phone_signin = (Button) rootView.findViewById(R.id.PhoneAccessBtn);

        mail_signup = (Button) rootView.findViewById(R.id.EmailRegisterBtn);

        signup_txt = (TextView) rootView.findViewById(R.id.signupText);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        access_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isUsernameValid = checkUsername();
                boolean isPasswordValid = checkPassword();

                if (isUsernameValid && isPasswordValid) {
                    Activity a = getActivity();
                    if (a instanceof Frag1Observer) {
                        Frag1Observer observer = (Frag1Observer) a;
                        Intent intent = new Intent(Constants.SIGNIN);
                        intent.putExtra("username", username.getText().toString());
                        intent.putExtra("password", password.getText().toString());
                        observer.notifyActionLogin(intent);
                    }
                }

            }
        });

        /*signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Activity a=getActivity();
                if (a instanceof Frag1Observer) {
                    Frag1Observer observer = (Frag1Observer) a;
                    Intent intent = new Intent(Constants.CHANGE_FRAGMENT);
                    observer.notifyActionLogin(intent);
                }


            }
        });*/

        mail_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment mailFragment = new MailDialogFragment();
                mailFragment.show(getChildFragmentManager(),"MailDialog");


            }
        });

        phone_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment phoneDialogFragment = new PhoneDialogFragment();
                phoneDialogFragment.show(getChildFragmentManager(),"PhoneDialog");


            }
        });

        mail_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment mailFragment = new MailDialogFragment();
                mailFragment.show(getChildFragmentManager(),"MailDialogSignup");

            }
        });








    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private boolean checkUsername(){

        if (username.getText().length()==0) {
            username.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            username.setError("");
            return true;
        }

    }

    private boolean checkPassword(){

        if (password.getText().length()==0) {
            password.setError(getString(R.string.not_empty));
            return false;
        }
        else {
            password.setError("");
            return true;
        }

    }
}
