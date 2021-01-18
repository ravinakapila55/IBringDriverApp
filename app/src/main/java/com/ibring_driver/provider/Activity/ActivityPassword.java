package com.ibring_driver.provider.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.AppHelper;
import com.ibring_driver.provider.Helper.ConnectionHelper;
import com.ibring_driver.provider.Helper.CustomDialog;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.Utilities;
import com.ibring_driver.provider.XuberServicesApplication;
import com.ibring_driver.provider.retrofit.RetrofitResponse;
import com.ibring_driver.provider.retrofit.RetrofitService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ibring_driver.provider.XuberServicesApplication.trimMessage;

public class ActivityPassword extends AppCompatActivity implements RetrofitResponse
{
    public Context context = ActivityPassword.this;
    public Activity activity = ActivityPassword.this;
    ConnectionHelper helper;
    Boolean isInternet;
    EditText password, email;

    TextView forgetPasswordTxt;

    TextView signInBtn;
    TextView tvSignup;
    RelativeLayout rlLogin;
    LinearLayout signUpLayout;

    CustomDialog customDialog;
    String TAG = "ActivityPassword";
    String device_token, device_UDID;
    Utilities utils = new Utilities();

    ImageView ivEye;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityPassword.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(ActivityPassword.this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(ActivityPassword.this, R.color.colorPrimary));
        }

     /*   Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(ActivityPassword.this.getApplication(), "7e86d1ca");*/

        /*setContentView(R.layout.activity_password);*/
        setContentView(R.layout.sign_in);
        findViewByIdandInit();
        GetToken();
        if (Build.VERSION.SDK_INT > 15)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        signInBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Utilities.hideKeypad(ActivityPassword.this,signInBtn);
                if (email.getText().toString().equals(""))
                {
//                    displayMessage(getString(R.string.email_validation));
                    Toast.makeText(ActivityPassword.this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
                }
                else if (!email.getText().toString().trim().matches(Patterns.EMAIL_ADDRESS.pattern()))
                {
                    Toast.makeText(ActivityPassword.this,"Please enter valid Email Address",
                            Toast.LENGTH_SHORT).show();
                }

                else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase
                        (getString(R.string.password_txt)))
                {
//                    displayMessage(getString(R.string.password_validation));
                    Toast.makeText(ActivityPassword.this, getString(R.string.password_validation), Toast.LENGTH_SHORT).show();

                }
                else if (password.getText().toString().length() < 6)
                {
//                    displayMessage(getString(R.string.passwd_length));
                    Toast.makeText(ActivityPassword.this, getString(R.string.passwd_length), Toast.LENGTH_SHORT).show();
                } else
                 {
//                    signIn();
                    callLoginApi();
                 }
            }
        });

        rlLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Utilities.hideKeypad(ActivityPassword.this,rlLogin);
                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
//                    displayMessage(getString(R.string.email_validation));
                    Toast.makeText(ActivityPassword.this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();

                }
                else if (!email.getText().toString().trim().matches(Patterns.EMAIL_ADDRESS.pattern()))
                {
                    Toast.makeText(ActivityPassword.this,"Please enter valid Email Address", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().equals("")) {
//                    displayMessage(getString(R.string.password_validation));
                    Toast.makeText(ActivityPassword.this, getString(R.string.password_validation), Toast.LENGTH_SHORT).show();

                }  else {
//                    signIn();
                    callLoginApi();

                }
            }
        });

        forgetPasswordTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ForgetPassword.class);
                startActivity(intent);
            }
        });

      /*  signUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getApplicationContext(), "from", "password");
                SharedHelper.putKey(context, "password", "");
                Intent mainIntent = new Intent(activity, RegisterActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
*/

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getApplicationContext(), "from", "password");
                SharedHelper.putKey(context, "password", "");
                Intent mainIntent = new Intent(activity, RegisterActivity.class);
                mainIntent.putExtra("ADD_NEW",0);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });





/*
        forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(context,"password", "");
                Intent mainIntent = new Intent(activity, ForgetPassword.class);
                startActivity(mainIntent);
            }
        });
*/


    }

    public void callLoginApi()
    {
        try
        {
            JSONObject object=new JSONObject();
            object.put("email", email.getText().toString());
            object.put("password", password.getText().toString());
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", device_token);
            Log.e("InputToLoginAPI", "" + object);

            new RetrofitService(this, this, URLHelper.LOGIN ,
                    object,
                    700, 2,"1").callService(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void signIn()
    {
        if (isInternet)
        {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

//                object.put("grant_type", "password");
//                object.put("CLIENT_ID", URLHelper.CLIENT_ID);
//                object.put("CLIENT_SECRET_KEY", URLHelper.CLIENT_SECRET_KEY);
                object.put("email", email.getText().toString());
                object.put("password", password.getText().toString());
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                Log.e("InputToLoginAPI", "" + object);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLHelper.LOGIN, object, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    customDialog.dismiss();
                    utils.print("SignUpResponse ", response.toString());

                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", "Bearer");
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(response.optString("picture")));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "description", response.optString("description"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));

                    if (!response.optString("currency").equalsIgnoreCase("") ||
                            !response.optString("currency").equalsIgnoreCase("null"))
                    {
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    } else
                    {
                        SharedHelper.putKey(context, "currency", "$");
                    }
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                    getProfile();

                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null)
                    {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                                try {
//                                    displayMessage(errorObj.optString("error"));
                                    Toast.makeText(ActivityPassword.this, errorObj.optString("error"), Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                                }
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    displayMessage(json);
                                    Toast.makeText(ActivityPassword.this,json, Toast.LENGTH_SHORT).show();

                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                    Toast.makeText(ActivityPassword.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                                }
                            }
                            else
                            {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(ActivityPassword.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e)
                        {
//                            displayMessage(getString(R.string.something_went_wrong));
                            Toast.makeText(ActivityPassword.this,
                                    getString(R.string.something_went_wrong_net),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        }
        else
        {
//            displayMessage(getString(R.string.something_went_wrong_net));
            Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

    }

    public void getProfile()
    {
        if (isInternet) {

            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            Log.e("ProfileUrl ", URLHelper.PROVIDER_PROFILE);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.PROVIDER_PROFILE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    Log.e("GetProfileResponse ", response.toString());
                    try {
                        JSONObject service=response.getJSONObject("service");
                        JSONObject service_type=service .getJSONObject("service_type");
                        Log.e("ServiceTypevlaue ",service_type.getString("service_types"));
                        SharedHelper.putKey(context, "service_types", service_type.getString("service_types"));
                        GoToMainActivity();
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Log.e("Response ",response.statusCode+"");
                    Log.e("ResponseData ",response.data+"");
                    if (response != null && response.data != null) {
                        try {
                          //  JSONObject errorObj = new JSONObject(new String(response.data));

                           /* if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    Toast.makeText(ActivityPassword.this, errorObj.optString("message").toString(), Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                                }
                            } else*/

                           if (response.statusCode == 401)
                           {
                                refreshAccessToken();
                            } else if (response.statusCode == 422)
                            {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    displayMessage(json);
                                    Toast.makeText(ActivityPassword.this, json.toString(), Toast.LENGTH_SHORT).show();

                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                    Toast.makeText(ActivityPassword.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                                }

                            } else if (response.statusCode == 503) {
//                                displayMessage(getString(R.string.server_down));
                                Toast.makeText(ActivityPassword.this, getString(R.string.server_down), Toast.LENGTH_SHORT).show();

                            } else {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(ActivityPassword.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
//                            displayMessage(getString(R.string.something_went_wrong));
                            Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                    utils.print("Authoization", "Bearer" + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }
        else {
            Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }

    public void showVerificationPopUp()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.payment_done_success, null);

        TextView tvLabel=(TextView)dialogView.findViewById(R.id.tvLabel);
        TextView tvCancel=(TextView)dialogView.findViewById(R.id.tvCancel);
        TextView tvOk=(TextView)dialogView.findViewById(R.id.tvOk);
        ImageView ivLabel=(ImageView) dialogView.findViewById(R.id.ivLabel);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;


        tvLabel.setText("Your account is not activate. \n please check your email to  activate the account");
        tvCancel.setVisibility(View.GONE);
        tvOk.setVisibility(View.GONE);
        ivLabel.setImageDrawable(getResources().getDrawable(R.drawable.cancel_req_icon));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        },3000);

        alertDialog.show();
    }


    private void refreshAccessToken()
    {
        if (isInternet)
        {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN,
                    object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context, "loggedIn","false");
                        GoToBeginActivity();
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

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        }
        else
        {
//            displayMessage(getString(R.string.something_went_wrong_net));
            Toast.makeText(ActivityPassword.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

    }

    public void findViewByIdandInit()
    {
        password = (EditText) findViewById(R.id.password);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        forgetPasswordTxt = (TextView) findViewById(R.id.forgetPasswordTxt);
        signInBtn = (TextView) findViewById(R.id.signInBtn);
        rlLogin = (RelativeLayout) findViewById(R.id.rlLogin);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        ivEye = (ImageView) findViewById(R.id.ivEye);
        signUpLayout = (LinearLayout) findViewById(R.id.lnrRegister);
        email = (EditText) findViewById(R.id.email);

        password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });



        ivEye.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    ivEye.setImageResource(R.drawable.hide_eye);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(password.length());

                }
                else
                {
                    ivEye.setImageResource(R.drawable.visible_eye);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(password.length());
                }
            }
        });

    }

    public void GoToBeginActivity()
    {
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    public void displayMessage(String toastString)
    {
        try {
            if (getCurrentFocus() != null)
            {
                Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(ActivityPassword.this, R.color.black));
               /* TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(ActivityPassword.this, R.color.white));*/
                snackbar.show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void GoToMainActivity()
    {
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    /* @Override
    public void onBackPressed() {
        SharedHelper.putKey(context,"password", "");
        Intent mainIntent = new Intent(activity, ActivityEmail.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }
    */

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void GetToken()
    {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        }
        catch (Exception e)
        {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }
        try
        {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        }
        catch (Exception e)
        {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 700:
                Log.e("ResponseLogin ",response);
                try {
                    JSONObject object=new JSONObject(response);
                    Log.e("object ",object.toString());

                    if(object.getString("status").equalsIgnoreCase("error"))
                    {
                        Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                        email.setText("");
                        password.setText("");
                    }

                    else
                    {
                        if (object.getString("email_verified_at").equalsIgnoreCase("null"))
                        {
                            showVerificationPopUp();
                        }
                        else
                        {
//                            JSONObject result=object.getJSONObject("result");
                            SharedHelper.putKey(context, "access_token", object.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token", object.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type", "Bearer");
                            SharedHelper.putKey(context, "id", object.optString("id"));
                            SharedHelper.putKey(context, "first_name", object.optString("first_name"));
                            SharedHelper.putKey(context, "last_name", object.optString("last_name"));
                            SharedHelper.putKey(context, "email", object.optString("email"));
                            SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(object.optString("picture")));
                            SharedHelper.putKey(context, "gender", object.optString("gender"));
                            SharedHelper.putKey(context, "mobile", object.optString("mobile"));
                            SharedHelper.putKey(context, "description", object.optString("description"));
                            SharedHelper.putKey(context, "wallet_balance", object.optString("wallet_balance"));
                            SharedHelper.putKey(context, "payment_mode", object.optString("payment_mode"));

                            if (!object.optString("currency").equalsIgnoreCase("") ||
                                    !object.optString("currency").equalsIgnoreCase("null"))
                            {
                                SharedHelper.putKey(context, "currency", object.optString("currency"));
                            } else
                            {
                                SharedHelper.putKey(context, "currency", "$");
                            }
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                            getProfile();
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
//                  displayMessage("Something Went Wrong");
                    Toast.makeText(ActivityPassword.this,getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
