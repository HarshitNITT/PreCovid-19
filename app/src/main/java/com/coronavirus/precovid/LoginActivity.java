package com.coronavirus.precovid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

;

public class LoginActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

//    public static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(7.798000, 68.14712), new LatLng(37.090000, 97.34466));
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private boolean  validated=true;
    @BindView(R.id.input_email)
    TextInputEditText _emailText;

    @BindView(R.id.btn_login)
    Button _loginButton;
    FirebaseAuth mAuth;
    private SharedPreferences sharedpreferences;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(getApplicationContext());
       mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.map1).setAlpha(0.17f);
        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("Your_ID_TOKEN")
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        sharedpreferences = getApplicationContext().getSharedPreferences("Preferences", 0);;
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
       MaterialButton bt = (MaterialButton) findViewById(R.id.btn_login);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        ButterKnife.bind(this);
        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);
        String login = sharedpreferences.getString("LOGIN", null);


        if (login != null) {

            Intent intent = new Intent(getApplicationContext(), MapsActivityCurrentPlace.class);
            intent.putExtra("Username",login);
            startActivityForResult(intent, REQUEST_SIGNUP);
        }

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {

                notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!");
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("onVerificationCompleted", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = task.getResult().getUser();
                                    Log.d("Sign in with phone auth", "Username of user " + user.getPhoneNumber());
                                    Intent intent = new Intent(getApplicationContext(), MapsActivityCurrentPlace.class);
                                    intent.putExtra("Username",user.getPhoneNumber());
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("LOGIN", user.getPhoneNumber());
                                    editor.apply();
                                    startActivity(intent);

                                } else {
                                    notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!");
                                }
                            }
                        });
            }


            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w("onVerificationFailed", "onVerificationFailed", e);



                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException" + e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e("Exception:", "FirebaseTooManyRequestsException" + e);

                }

                notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!");
            }
            public void notifyUserAndRetry(String s)
            {
               Toast.makeText(LoginActivity.this,s,Toast.LENGTH_LONG);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("onCodeSent", "onCodeSent:" + verificationId);
                Log.i("Verification code:", verificationId);
            }
        };


            bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phoneNumber = ((TextInputEditText)findViewById(R.id.input_email1)).getText().toString();
                if(phoneNumber.length()<10  ) {
                    ((TextInputEditText)findViewById(R.id.input_email1)).setText("");
                    ((TextInputEditText)findViewById(R.id.input_email1)).setHint("please enter valid phone number");
                    ((TextInputEditText)findViewById(R.id.input_email1)).setHintTextColor(Color.parseColor("#444444"));
                    return;
                }
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginActivity.this,               // Activity (for callback binding)
                        mCallbacks);
            }
        });

    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == 234) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.146633,79.088860),18));
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();Intent intent = new Intent(getApplicationContext(), MapsActivityCurrentPlace.class);
                            intent.putExtra("Username",user.getEmail());
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("LOGIN", user.getEmail());
                            editor.apply();
                            startActivity(intent);
//                            Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, 234);
    }


    }
