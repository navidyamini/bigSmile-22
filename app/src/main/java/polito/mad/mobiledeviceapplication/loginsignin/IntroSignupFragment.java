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
import android.widget.Toast;

import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;

/**
 * Created by user on 22/04/2018.
 */

public class IntroSignupFragment extends Fragment {

    private Button register_btn, mail_signup;
    private TextView signin_txt;
    private EditText username,password;

    interface Frag2Observer {
        void notifyActionSignup(Intent intent);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_intro_signup, container, false);

        username = (EditText) rootView.findViewById(R.id.username);
        password = (EditText) rootView.findViewById(R.id.password);

        register_btn = (Button) rootView.findViewById(R.id.registerButton);
        mail_signup = (Button) rootView.findViewById(R.id.EmailRegisterBtn);

        signin_txt = (TextView) rootView.findViewById(R.id.signinText);

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    Activity a = getActivity();
                    if (a instanceof IntroSignupFragment.Frag2Observer) {
                        IntroSignupFragment.Frag2Observer observer = (IntroSignupFragment.Frag2Observer) a;
                        Intent intent = new Intent(Constants.SIGNUP);
                        intent.putExtra("username", username.getText().toString());
                        intent.putExtra("password", password.getText().toString());
                        observer.notifyActionSignup(intent);
                    }
                }
                else{
                    Toast.makeText(getContext(),"Username and password fields must not be empty",Toast.LENGTH_LONG).show();

                }
            }
        });

        mail_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment mailFragment = new MailDialogFragment();
                mailFragment.show(getChildFragmentManager(),"MailDialogSignup");

            }
        });

        signin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a=getActivity();
                if (a instanceof IntroSignupFragment.Frag2Observer) {
                    IntroSignupFragment.Frag2Observer observer = (IntroSignupFragment.Frag2Observer) a;
                    Intent intent = new Intent(Constants.CHANGE_FRAGMENT);
                    observer.notifyActionSignup(intent);
                }
            }
        });


    }
}
