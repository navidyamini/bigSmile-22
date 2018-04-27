package polito.mad.mobiledeviceapplication.loginsignin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import polito.mad.mobiledeviceapplication.MainActivity;
import polito.mad.mobiledeviceapplication.R;
import polito.mad.mobiledeviceapplication.utils.Constants;
import polito.mad.mobiledeviceapplication.utils.User;

/**
 * Created by user on 20/04/2018.
 */

public class LoginSigninActivity extends FragmentActivity implements IntroLoginFragment.Frag1Observer, IntroSignupFragment.Frag2Observer {

    private FirebaseAuth mAuth;
    private Button signup,logout,signin;
    private final static String TAG = "Firebase";
    private DatabaseReference mDatabase;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks_login = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);

            signInWithPhoneAuthCredential(credential);


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            Fragment frag1 = (Fragment) getSupportFragmentManager().findFragmentById(R.id.container);
            final PhoneDialogFragment frag = (PhoneDialogFragment)frag1.getChildFragmentManager().findFragmentByTag("PhoneDialog");

            if (frag!=null && frag.isVisible()){
                frag.dismiss();
            }

        }

        @Override
        public void onCodeSent(String verificationId,
                PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            mVerificationId = verificationId;
            mResendToken = token;


            Fragment frag1 = (Fragment) getSupportFragmentManager().findFragmentById(R.id.container);
            final PhoneDialogFragment frag = (PhoneDialogFragment)frag1.getChildFragmentManager().findFragmentByTag("PhoneDialog");
            if (frag!=null && frag.isVisible()){

                frag.getView().findViewById(R.id.loadingProgressBar).setVisibility(View.INVISIBLE);
                frag.getView().findViewById(R.id.code_lay).setVisibility(View.VISIBLE);
                frag.getView().findViewById(R.id.phone).setEnabled(false);

                ((Button)frag.getView().findViewById(R.id.phone_button)).setText("Send code");
                ((Button)frag.getView().findViewById(R.id.phone_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (((TextInputEditText)frag.getView().findViewById(R.id.code)).getText().toString().equals(""))
                            ((TextInputLayout)frag.getView().findViewById(R.id.code_lay)).setError("This field cannot be empty");
                        else {
                            frag.getView().findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
                            ((Button)frag.getView().findViewById(R.id.phone_button)).setText("");
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, ((TextInputEditText) frag.getView().findViewById(R.id.code)).getText().toString());
                            signInWithPhoneAuthCredential(credential);
                        }
                    }
                });

            }
        }
    };


    @Override
    public void notifyActionLogin(final Intent intent) {

        if (Constants.SIGNIN.equals(intent.getAction())){

            if (mDatabase==null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren().iterator().next().getChildren()) {
                            User user = child.getValue(User.class);
                            if (user != null) {
                                if (user.username.equals(intent.getStringExtra("username"))
                                        && user.password.equals(intent.getStringExtra("password"))) {

                                    getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("UID",child.getKey()).apply();
                                    getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("username",user.username).apply();
                                    getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("password",user.password).apply();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    System.out.println("WOW OK");
                                } else
                                    System.out.println("USER NOT FOUND");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "USER NOT FOUND", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

                }
            };

            mDatabase.addListenerForSingleValueEvent(postListener);


        } else if (Constants.SIGNIN_EMAIL.equals(intent.getAction())){


            if (mAuth!=null) {

                mAuth.signInWithEmailAndPassword(intent.getStringExtra("email"), intent.getStringExtra("password"))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginSigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    //if (task.getException().getClass().equals("com.google.firebase.auth.FirebaseAuthInvalidUserException"))
                                    String error = ((FirebaseAuthException)task.getException()).getErrorCode();
                                    if(error.equals(FirebaseError.ERROR_INVALID_CREDENTIAL))
                                        Toast.makeText(getApplicationContext(),"Credentials are bad formatted",Toast.LENGTH_LONG).show();
                                    if(error.equals(FirebaseError.ERROR_USER_NOT_FOUND))
                                        Toast.makeText(getApplicationContext(),"This email is not present in our systems",Toast.LENGTH_LONG).show();
                                    if (error.equals(FirebaseError.ERROR_WRONG_PASSWORD))
                                        Toast.makeText(getApplicationContext(), "The password is incorrect", Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });


            }


        } else if (Constants.SIGNIN_PHONE.equals(intent.getAction())){


            if (mAuth!=null) {
                mAuth.useAppLanguage();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        intent.getStringExtra("phone"),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks_login);        // OnVerificationStateChangedCallbacks

                Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.container);
                PhoneDialogFragment fragment1 = (PhoneDialogFragment) fragment.getChildFragmentManager().findFragmentByTag("PhoneDialog");
                fragment1.getView().findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
                ((Button)fragment1.getView().findViewById(R.id.phone_button)).setText("");
            }

        } else if (Constants.CHANGE_FRAGMENT.equals(intent.getAction())){


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new IntroSignupFragment());
            transaction.addToBackStack(null);
            transaction.commit();



        }
    }

    @Override
    public void notifyActionSignup(final Intent intent) {

        if (Constants.SIGNUP.equals(intent.getAction().toString())){

            if (mDatabase==null)
                mDatabase = FirebaseDatabase.getInstance().getReference();

            User user = new User(intent.getStringExtra("username"), intent.getStringExtra("password"),"","","","","","","","");

            mDatabase.child("users").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        if (mDatabase==null)
                            mDatabase = FirebaseDatabase.getInstance().getReference();

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child : dataSnapshot.getChildren().iterator().next().getChildren())
                                {
                                    User user = child.getValue(User.class);
                                    if (user.username.equals(intent.getStringExtra("username"))
                                            && user.password.equals(intent.getStringExtra("password"))) {

                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("UID",child.getKey()).apply();
                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("username",user.username).apply();
                                        getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).edit().putString("password",user.password).apply();

                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

                            }
                        };

                        mDatabase.addListenerForSingleValueEvent(postListener);


                    }

                }
            });

        } else if (Constants.SIGNUP_EMAIL.equals(intent.getAction().toString())){

            mAuth.createUserWithEmailAndPassword(intent.getStringExtra("email"), intent.getStringExtra("password"))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginSigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

        } else if (Constants.CHANGE_FRAGMENT.equals(intent.getAction())){

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            getSupportFragmentManager().popBackStack();
            transaction.replace(R.id.container, new IntroLoginFragment());
            transaction.addToBackStack(null);
            transaction.commit();



    }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        mAuth = FirebaseAuth.getInstance();


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,new IntroLoginFragment());
        transaction.commit();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null || !getSharedPreferences(Constants.PREFERENCE_FILE,MODE_PRIVATE).getString("UID","").equals("")) {
            //ALREADY LOGGED
            //System.out.println("User " + currentUser.getPhoneNumber());
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }





    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Fragment frag1 = (Fragment) getSupportFragmentManager().findFragmentById(R.id.container);
                        final PhoneDialogFragment frag = (PhoneDialogFragment)frag1.getChildFragmentManager().findFragmentByTag("PhoneDialog");

                        if (frag!=null && frag.isVisible()){
                            frag.dismiss();
                        }
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                Toast.makeText(getApplicationContext(),"The code is invalid",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }



}
