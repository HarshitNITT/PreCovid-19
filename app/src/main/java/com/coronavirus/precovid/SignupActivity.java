package com.coronavirus.precovid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends Activity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindView(R.id.hour) TextView _hourText;
    @BindView(R.id.minute) TextView _minuteText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");
        final RequestQueue queue = Volley.newRequestQueue(this);
//        if (!validate()) {
//            onSignupFailed();
//            return;
//        }

        _signupButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
        final RequestQueue queue1 = Volley.newRequestQueue(this);
        String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        final String hours= _hourText.getText().toString();
        final String minutes= _minuteText.getText().toString();
        String url = "https://y4ycjffei3.execute-api.us-east-2.amazonaws.com/testing/username" ;
        // TODO: Implement your own signup logic here.
        Map<String, String> params = new HashMap<String, String>();
        Log.d("msg",url+" url ");
        params.put("Username", email);
        params.put("password", password);
        final JsonObjectRequest stringRequest = new JsonObjectRequest(url,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
                        toast.show();

                        Map<String, String> params1 = new HashMap<String, String>();
                        params1.put("Username", email+"alarm");
                        params1.put("hour", hours);
                        params1.put("minute", minutes);
                        Log.d("msg",hours+" :debug "+minutes);
                        String url1 = "https://y4ycjffei3.execute-api.us-east-2.amazonaws.com/testing/alarm";
                        final JsonObjectRequest stringRequest1 = new JsonObjectRequest(url1,new JSONObject(params1),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Display the first 500 characters of the response string.
                                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
                                        toast.show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("succ", "successerr alarm "+error);
                                Toast toast = Toast.makeText(getApplicationContext(), "Didn't Work!!", Toast.LENGTH_LONG);
                                toast.show();

                            }
                        }) ;
                        queue1.add(stringRequest1);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("succ", "successerr "+error);
                Toast toast = Toast.makeText(getApplicationContext(), "Didn't Work!!", Toast.LENGTH_LONG);
                toast.show();

            }
        }) ;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

//        queue.stop();

//        setResult(RESULT_OK, null);
//        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.putExtra("key",email);
        startActivityForResult(intent,1);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
//                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
//        _signupButton.setEnabled(true);
//        Log.d("msg","Key is "+email);
//                                    startActivityForResult(intent, REQUEST_SIGNUP);
//        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        int hours=Integer.getInteger(_hourText.getText().toString());
        int minutes=Integer.getInteger(_minuteText.getText().toString());
        if(minutes<0 && minutes>60)
        {
            _hourText.setError("between 0 and 60 values");
            valid = false;

        }
        else {
            _hourText.setError(null);
        }
        if(hours<0 && hours>12)
        {
            _minuteText.setError("between 0 and 12 values");
            valid = false;

        }
        else {
            _minuteText.setError(null);
        }
        return valid;
    }
}
