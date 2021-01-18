package com.ibring_driver.provider.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.CustomDialog;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.Utilities;
import com.ibring_driver.provider.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgetPassword extends AppCompatActivity {

    ImageView backArrow;
    EditText email;
    Button nextICON;
    CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_forget_password);
        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeypad(ForgetPassword.this,nextICON);

                if (email.getText().toString().isEmpty()) {
//                    displayMessage("Please enter your Email Address");
                    Toast.makeText(ForgetPassword.this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!email.getText().toString().trim().matches(Patterns.EMAIL_ADDRESS.pattern()))
                {
                    Toast.makeText(ForgetPassword.this,"Please enter valid Email Address",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    forgetPassword();
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ForgetPassword.this, ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                ForgetPassword.this.finish();
            }
        });

    }


    private void forgetPassword() {
        customDialog = new CustomDialog(ForgetPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {

            SharedHelper.putKey(ForgetPassword.this, "email", email.getText().toString());
            object.put("email", email.getText().toString());
            Log.e("InputToLoginAPI", "" + object);
            Log.e("InputToLoginAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.FORGET_PASSWORD, object,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("ResponseForgotPswd ", "" + response.toString());

                try {
                    if (response.getString("message").equalsIgnoreCase("OTP sent to your email!"))
                    {

                        displayMessage("" + response.optString("message"));

                        JSONObject userObj = response.optJSONObject("provider");

                        SharedHelper.putKey(ForgetPassword.this, "reset_id", "" + userObj.optInt("id"));

                        SharedHelper.putKey(ForgetPassword.this, "otp", "" + userObj.optInt("otp"));

                        Intent resetIntent = new Intent(ForgetPassword.this, OTPActivity.class);
                        startActivity(resetIntent);
                        finish();
                    }
                    else {
                        Toast.makeText(ForgetPassword.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest", "" + error);
                Log.e("MyTestError", "" + error.networkResponse);
                Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }

                        } else if (response.statusCode == 422) {

                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage("Please try again.");
                            }

                        } else {
                            displayMessage("Please try again.");
                        }

                    } catch (Exception e) {
                        displayMessage("Something went wrong.");
                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void findViewById() {
        email = (EditText) findViewById(R.id.email);
        nextICON = (Button) findViewById(R.id.nextIcon);
        backArrow = (ImageView) findViewById(R.id.backArrow);
    }

    public void displayMessage(String toastString) {
        try {
            if (getCurrentFocus() != null) {
                Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
       /* TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.white));*/
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(ForgetPassword.this, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        ForgetPassword.this.finish();
    }
}
