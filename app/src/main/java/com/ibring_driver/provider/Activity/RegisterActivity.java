package com.ibring_driver.provider.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.ibring_driver.provider.Models.MatrixServices;
import com.ibring_driver.provider.Models.OtherServices;
import com.ibring_driver.provider.Models.SignupData;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.KeyHelper;
import com.ibring_driver.provider.Utils.Utilities;
import com.ibring_driver.provider.XuberServicesApplication;
import com.ibring_driver.provider.retrofit.RetrofitResponse;
import com.ibring_driver.provider.retrofit.RetrofitService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ibring_driver.provider.XuberServicesApplication.trimMessage;

public class RegisterActivity extends AppCompatActivity implements RetrofitResponse
{
    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    String device_token, device_UDID;
    ImageView backArrow;
    EditText email, first_name, last_name, mobile_no, password,cpassword;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();
    Button signUpBtn;
    TextView tvSignup;
    TextView tvLogin11;
    LinearLayout signInLayout;
    Spinner sp_service;
    Spinner sp_sub_service;
    RelativeLayout rlSubService;
    View viewwww1;
    ImageView ivEye,ivCEye;

    RelativeLayout lnrRegister;

    XuberServicesApplication xuberServicesApplication;


//    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*-_]).{6,20})";
    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
        }

//        setContentView(R.layout.activity_register);
        setContentView(R.layout.signup);
        findViewById();

        xuberServicesApplication=(XuberServicesApplication)getApplication();

        if (getIntent().getIntExtra("ADD_NEW",0)==0)
        {
            xuberServicesApplication.signupData=new SignupData();
        }
        else
        {
            first_name.setText(xuberServicesApplication.signupData.getFname());
            last_name.setText(xuberServicesApplication.signupData.getLname());
            email.setText(xuberServicesApplication.signupData.getEmail());
            mobile_no.setText(xuberServicesApplication.signupData.getPhone());
            password.setText(xuberServicesApplication.signupData.getPassword());
            cpassword.setText(xuberServicesApplication.signupData.getCpassword());
        }

        callServiceList();
        GetToken();

        if (Build.VERSION.SDK_INT > 15)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        lnrRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());
                pattern = Pattern.compile(PASSWORD_PATTERN);
                matcher=pattern.matcher(password.getText().toString().trim());
                Log.e("matcher",""+matcher+"");

                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id)))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
                }

                else if (!email.getText().toString().trim().matches(Patterns.EMAIL_ADDRESS.pattern()))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter valid Email Address",
                            Toast.LENGTH_SHORT).show();
                }

                else if (first_name.getText().toString().equals("") ||
                        first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name)))
                {
                    Toast.makeText(RegisterActivity.this,"First Name is Required", Toast.LENGTH_SHORT).show();
                }
                else if (first_name.getText().toString().trim().length()<3 || first_name.getText().toString().trim().length()>30)
                {
                    Toast.makeText(RegisterActivity.this, "First Name must be between 3 and 30 characters", Toast.LENGTH_SHORT).show();
                }
                else if (firstName.matches())
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.first_name_no_number), Toast.LENGTH_SHORT).show();
                }
                else if (last_name.getText().toString().equals("") ||
                        last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name)))
                {
                    Toast.makeText(RegisterActivity.this, "Last Name is Required", Toast.LENGTH_SHORT).show();
                }
                else if (last_name.getText().toString().trim().length()<3 || last_name.getText().toString().trim().length()>30)
                {
                    Toast.makeText(RegisterActivity.this, "Last Name must be between 3 and 30 characters", Toast.LENGTH_SHORT).show();
                }
                else if (lastName.matches())
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.last_name_no_number), Toast.LENGTH_SHORT).show();
                }
                else if (mobile_no.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.mobile_number_empty), Toast.LENGTH_SHORT).show();
                }
                else if (mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 15)
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.mobile_no_validation), Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().equalsIgnoreCase("000000000000000"))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }

                else if (password.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.password_validation), Toast.LENGTH_SHORT).show();
                }

                else if (password.getText().toString().length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.passwd_length), Toast.LENGTH_SHORT).show();
                }
                else if (!matcher.matches())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter atleast one small letter, one capital letter, one digit and one special character.",
                            Toast.LENGTH_SHORT).show();

                }
                else if (cpassword.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.cpassword_validation), Toast.LENGTH_SHORT).show();
                }

                else if (!(cpassword.getText().toString().trim().equalsIgnoreCase(password.getText().toString().trim())))
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.not_equal_pswd), Toast.LENGTH_SHORT).show();
//                    password.setText("");
                    cpassword.setText("");

                }

                else if (serviceID.equalsIgnoreCase(""))
                {
                    Toast.makeText(RegisterActivity.this, "Please choose Service Type", Toast.LENGTH_SHORT).show();
                }
                else
                 {
                     xuberServicesApplication.signupData.setFname(first_name.getText().toString().trim());
                     xuberServicesApplication.signupData.setLname(last_name.getText().toString().trim());
                     xuberServicesApplication.signupData.setEmail(email.getText().toString().trim());
                     xuberServicesApplication.signupData.setPassword(password.getText().toString().trim());
                     xuberServicesApplication.signupData.setCpassword(cpassword.getText().toString().trim());
                     xuberServicesApplication.signupData.setPhone(mobile_no.getText().toString().trim());
                     xuberServicesApplication.signupData.setService_id(serviceID);
                     xuberServicesApplication.signupData.setService_type(serviceType);

                     //taxi service
                     if (serviceID.equalsIgnoreCase("15") || serviceID.equalsIgnoreCase("18"))
                     {
                         Intent intent=new Intent(RegisterActivity.this,AddCarDetails.class);
                         intent.putExtra("key","direct");
                         intent.putExtra("service",serviceID);
                         startActivity(intent);
                     }

                     else if(serviceID.equalsIgnoreCase("19"))
                     {
                         callRegister();
                     }


                     /*//food service
                     else if (serviceID.equalsIgnoreCase("18"))
                     {
//                         registerAPI();
                         callRegister();
                     }*/
                     else
                     {
//                         Toast.makeText(RegisterActivity.this, "This service is not available.", Toast.LENGTH_SHORT).show();
                         callRegister();
                     }


                   /* if (isInternet)
                    {
                        registerAPI();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong_net),
                                Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        });

        tvLogin11.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public void findViewById()
    {
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        password = (EditText) findViewById(R.id.password);
        cpassword = (EditText) findViewById(R.id.cpassword);
        lnrRegister = (RelativeLayout) findViewById(R.id.lnrRegister);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        sp_service = (Spinner) findViewById(R.id.sp_service);
        sp_sub_service = (Spinner) findViewById(R.id.sp_sub_service);
        rlSubService = (RelativeLayout) findViewById(R.id.rlSubService);
        tvLogin11 = (TextView) findViewById(R.id.tvLogin11);
        helper = new ConnectionHelper(context);


        ivEye = (ImageView) findViewById(R.id.ivEye);
        ivCEye = (ImageView) findViewById(R.id.ivCEye);


        isInternet = helper.isConnectingToInternet();

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

        cpassword.setCustomSelectionActionModeCallback(new ActionMode.Callback()
        {

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

        ivCEye.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("ConfirmClick ","confirm");
                if (cpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    ivCEye.setImageResource(R.drawable.hide_eye);
                    cpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    cpassword.setSelection(cpassword.length());
                }
                else
                {
                    ivCEye.setImageResource(R.drawable.visible_eye);
                    cpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    cpassword.setSelection(cpassword.length());
                }
            }
        });

        setService();
    }

    String serviceID="";
    String serviceType="";

    public void setService()
    {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.custom_spinner,service)
        {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position ==0)
                {
                    // Set the item text color
//                    tv.setTextColor(Color.parseColor("#0d4bb1"));
                    tv.setTextColor(Color.parseColor("#3D404E"));
                }
                else
                {
                    // Set the alternate item text color
                    tv.setTextColor(Color.parseColor("#3D404E"));
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner);
        sp_service.setAdapter(spinnerArrayAdapter);


        sp_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (sp_service.getSelectedItem().equals("Taxi Service"))
                {
                    serviceID="15";
                    serviceType="taxi_service";
                    tvSignup.setText("NEXT");
                }
                else if (sp_service.getSelectedItem().equals("Courier Service"))
                {
                    serviceID="19";
                    serviceType="courier_service";
                    tvSignup.setText("SUBMIT");
                }
                else
                {
                    serviceID="18";
                    serviceType="food_service";
                    tvSignup.setText("NEXT");
                }

                Log.e("selectedCOlor ",serviceID);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    String[] service={"Choose a Service","Taxi Service","Courier Service","Food Service","Other Services"};

    public void callRegister()
    {
            try
            {
                JSONObject object=new JSONObject();

                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("service_id", serviceID);
                object.put("service_type", serviceType);
                object.put("device_token", "" + device_token);
                object.put("login_by", "manual");
                object.put("first_name", first_name.getText().toString());
                object.put("last_name", last_name.getText().toString());
                object.put("email", email.getText().toString());
                object.put("password", password.getText().toString());
                object.put("password_confirmation", cpassword.getText().toString());
                object.put("mobile", mobile_no.getText().toString());

//                serviceID= xuberServicesApplication.signupData.getService_id();
                object.put("service_id", serviceID);
                object.put("service_type", serviceType);

                Log.e("InputToRegisterAPI ", "" + object.toString());

                new RetrofitService(this, this, URLHelper.REGISTER ,
                        object,
                        100, 2,"1").callService(true);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

            }

    }

    private void registerAPI()
    {
        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
       try
        {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("service_id", serviceID);
            object.put("service_type", serviceType);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", first_name.getText().toString());
            object.put("last_name", last_name.getText().toString());
            object.put("email", email.getText().toString());
            object.put("password", password.getText().toString());
            object.put("password_confirmation", cpassword.getText().toString());
            object.put("mobile", mobile_no.getText().toString());
            Log.e("InputToRegisterAPI", "" + object.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.REGISTER, object,
                new Response.Listener<JSONObject>()
         {
            @Override
            public void onResponse(JSONObject response)
            {
                customDialog.dismiss();
                utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
                signIn();
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

                if (response != null && response.data != null)
                {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try
                    {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500)
                        {
                            try
                            {
//                                displayMessage(errorObj.optString("message"));
                                Toast.makeText(RegisterActivity.this,errorObj.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
//                                displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(RegisterActivity.this,getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                        else if (response.statusCode == 401)
                        {
                            try
                            {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token"))
                                {
                                    //Call Refresh token
                                }
                                else
                                {
//                                    displayMessage(errorObj.optString("message"));
                                    Toast.makeText(RegisterActivity.this, errorObj.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
//                                displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (response.statusCode == 422)
                        {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null)
                            {
//                                displayMessage(json);
                                Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(RegisterActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                            }

                        } else
                            {
//                            displayMessage(getString(R.string.please_try_again));
                            Toast.makeText(RegisterActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e)
                    {
//                        displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void callServiceList()
    {
        new RetrofitService(this, this, URLHelper.GET_APP_SERVICES ,
                105, 1,"1").callService(true);
    }

    public void signIn()
    {
        if (isInternet)
        {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try
            {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("email", SharedHelper.getKey(RegisterActivity.this, "email"));
                object.put("password", SharedHelper.getKey(RegisterActivity.this, "password"));
                object.put("scope", "");
                utils.print("InputToLoginAPI", "" + object);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object,
                    new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "settings", "no");
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    customDialog.dismiss();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null)
                    {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500)
                            {
                                try
                                {
//                                    displayMessage(errorObj.optString("message"));
                                    Toast.makeText(RegisterActivity.this,errorObj.optString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(RegisterActivity.this,getString(R.string.something_went_wrong),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (response.statusCode == 401)
                            {
                                try
                                {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token"))
                                    {
                                        //Call Refresh token
                                    }
                                    else
                                    {
//                                        displayMessage(errorObj.optString("message"));
                                        Toast.makeText(RegisterActivity.this,errorObj.optString("message"),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e)
                                {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(RegisterActivity.this,getString(R.string.something_went_wrong),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (response.statusCode == 422)
                            {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null)
                                {
//                                    displayMessage(json);
                                    Toast.makeText(RegisterActivity.this,json, Toast.LENGTH_SHORT).show();
                                }
                                else
                               {
//                                    displayMessage(getString(R.string.please_try_again));
                                    Toast.makeText(RegisterActivity.this,getString(R.string.please_try_again),
                                            Toast.LENGTH_SHORT).show();
                               }
                            }
                            else
                           {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(RegisterActivity.this,getString(R.string.please_try_again),
                                        Toast.LENGTH_SHORT).show();
                           }
                        }
                        catch (Exception e)
                        {
//                            displayMessage(getString(R.string.something_went_wrong));
                            Toast.makeText(RegisterActivity.this,getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT).show();
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

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            //displayMessage(getString(R.string.something_went_wrong_net));
            Toast.makeText(RegisterActivity.this,getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();


        }

    }

    public void getProfile()
    {
        if (isInternet)
        {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URLHelper.PROVIDER_PROFILE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(RegisterActivity.this, "id", response.optString("id"));
                    SharedHelper.putKey(RegisterActivity.this, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(RegisterActivity.this, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(RegisterActivity.this, "email", response.optString("email"));
                    SharedHelper.putKey(context, "description", response.optString("description"));
                    SharedHelper.putKey(RegisterActivity.this, "picture", AppHelper.getImageUrl(response.optString("picture")));
                    SharedHelper.putKey(RegisterActivity.this, "gender", response.optString("gender"));
                    SharedHelper.putKey(RegisterActivity.this, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(RegisterActivity.this, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(RegisterActivity.this, "payment_mode", response.optString("payment_mode"));
                    if (!response.optString("currency").equalsIgnoreCase("") || !response.optString("currency").equalsIgnoreCase("null")) {
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    } else {
                        SharedHelper.putKey(context, "currency", "$");
                    }
                    SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));
                    GoToMainActivity();
                    //GoToSettingsStart();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
//                                    displayMessage(errorObj.optString("message"));
                                    Toast.makeText(RegisterActivity.this,errorObj.optString("message"), Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
//                                        displayMessage(errorObj.optString("message"));
                                        Toast.makeText(RegisterActivity.this,errorObj.optString("message"), Toast.LENGTH_SHORT).show();

                                    }
                                } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    displayMessage(json);
                                    Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_SHORT).show();
                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                    Toast.makeText(RegisterActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                                }

                            } else {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(RegisterActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
                            Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(RegisterActivity.this, KeyHelper.KEY_ACCESS_TOKEN));
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
//            displayMessage(getString(R.string.something_went_wrong_net));
            Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();

        }
    }


    public void GetToken()
    {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.e("@#@#@","device token"+device_token);
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

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.e("@#@#@","device uid"+device_UDID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }


    public void GoToMainActivity()
    {
//        Intent mainIntent = new Intent(RegisterActivity.this, Home.class);
        Intent mainIntent = new Intent(RegisterActivity.this, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }

    public void GoToSettingsStart()
    {
        Intent mainIntent = new Intent(activity, SettingsStartActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString)
    {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.black));
      /*  TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.white));*/
        snackbar.show();
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
    public void onBackPressed()
    {
        if (SharedHelper.getKey(context, "from").equalsIgnoreCase("email"))
        {
            Intent mainIntent = new Intent(RegisterActivity.this, ActivityEmail.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
           RegisterActivity.this.finish();
        }

        else
        {
            Intent mainIntent = new Intent(RegisterActivity.this, ActivityPassword.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:
                Log.e("ResponseServiceList ",response);

                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.e("jsonObject ",jsonObject.toString());

                    listMatrix.clear();
                    matrixName.clear();
                    listOthersd.clear();
                    otherName.clear();

                    matrixName.add("Choose a Service");
                    otherName.add("Choose  Other Services");

                    JSONArray matrix=jsonObject.getJSONArray("matrix");

                    if (matrix.length()>0)
                    {
                        for (int i = 0; i < matrix.length(); i++)
                        {
                            MatrixServices matrixServices = new MatrixServices();
                            JSONObject jsonObject1 = matrix.getJSONObject(i);

                            matrixServices.setId(jsonObject1.getString("id"));
                            matrixServices.setName(jsonObject1.getString("service_types"));

                            listMatrix.add(matrixServices);
//                            matrixName.add(jsonObject1.getString("service_types"));
                            matrixName.add(jsonObject1.getString("name"));
                        }
                    }

                    JSONArray others=jsonObject.getJSONArray("others");

                    if (others.length()>0)
                    {
                        for (int j = 0; j <others.length() ; j++)
                        {
                            OtherServices other=new OtherServices();
                            JSONObject jsonObject1=others.getJSONObject(j);

                            other.setId(jsonObject1.getString("id"));
                            other.setName(jsonObject1.getString("name"));

                            listOthersd.add(other);
                            otherName.add(jsonObject1.getString("name"));
                        }
                    }

                    matrixName.add(4,"Others");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_spinner,matrixName);
                    sp_service.setAdapter(adapter);

                        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                                this,R.layout.custom_spinner,matrixName)
                        {
                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent)
                            {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if(position ==0)
                                {
                                    // Set the item text color
                                    tv.setTextColor(Color.parseColor("#091046"));
                                }
                                else
                                {
                                    // Set the alternate item text color
                                    tv.setTextColor(Color.parseColor("#091046"));
                                }
                                return view;
                            }
                        };

                        spinnerArrayAdapter1.setDropDownViewResource(R.layout.custom_spinner);
                        sp_service.setAdapter(spinnerArrayAdapter1);

                        sp_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                            {
                                if (position==matrixName.size()-1)
                                {
                                    serviceID="";
                                    sp_sub_service.setVisibility(View.VISIBLE);
                                    rlSubService.setVisibility(View.VISIBLE);


                                    Log.e("Serviceid ",serviceID);
                                    tvSignup.setText("NEXT");

                                    Log.e("SubServiceLisdt ",listOthersd.size()+"");
                                    Log.e("name ",otherName.size()+"");
                                    setSubServiceAdapter();
                                }
                                else
                               {
                                    if (position==0)
                                    {
                                        serviceID="";
                                        Log.e("Serviceid ",serviceID);
                                        tvSignup.setText("NEXT");
                                    }
                                    else
                                    {
                                        serviceID=listMatrix.get(position-1).getId()+"";
                                        Log.e("Serviceid ",serviceID);
                                        sp_sub_service.setVisibility(View.GONE);
                                        rlSubService.setVisibility(View.GONE);
                                        if (serviceID.equalsIgnoreCase("19"))
                                        {
                                            tvSignup.setText("SUBMIT");
                                        }
                                        else
                                        {
                                            tvSignup.setText("NEXT");
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent)
                            {

                            }
                        });
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                break;

            case 100:
                try {
                    Log.e("SignUpResponseeeee ", response.toString());

                    JSONObject jsonObject=new JSONObject(response);


                    if (jsonObject.has("status"))
                    {
                        if (jsonObject.getString("status").equalsIgnoreCase("error"))
                        {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                        showVerificationPopUp();
                        }
                        else {
                            showVerificationPopUp();
                        }
                    }
                   /* else {
                        if (jsonObject.has("email"))
                        {
                            showVerificationPopUp();
                        }
                        else {
                            Toast.makeText(this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                        *//*SharedHelper.putKey(context, "access_token", jsonObject.optString("access_token"));
                        SharedHelper.putKey(context, "refresh_token", jsonObject.optString("refresh_token"));
                        SharedHelper.putKey(context, "token_type", jsonObject.optString("token_type"));
                        getProfile();*//*


                    }*/
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;
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

        tvLabel.setText("A verification e-mail has been sent. \n please check to activate your account");
        tvCancel.setVisibility(View.GONE);
        tvOk.setVisibility(View.GONE);
        ivLabel.setImageDrawable(getResources().getDrawable(R.drawable.cancel_req_icon));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GoToMainActivity();
            }
        },3000);



        alertDialog.show();
    }

    public void setSubServiceAdapter()
    {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.custom_spinner, otherName);
            sp_sub_service.setAdapter(adapter);


                                    final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                                            RegisterActivity.this,R.layout.custom_spinner,otherName)
                                    {
                                        @Override
                                        public View getDropDownView(int position, View convertView,
                                                                    ViewGroup parent)
                                        {
                                            View view = super.getDropDownView(position, convertView, parent);
                                            TextView tv = (TextView) view;
                                            if(position ==0)
                                            {
                                                // Set the item text color
                                                tv.setTextColor(Color.parseColor("#091046"));
                                            }
                                            else
                                            {
                                                // Set the alternate item text color
                                                tv.setTextColor(Color.parseColor("#091046"));
                                            }
                                            return view;
                                        }
                                    };

                                    spinnerArrayAdapter1.setDropDownViewResource(R.layout.custom_spinner);
                                    sp_sub_service.setAdapter(spinnerArrayAdapter1);

                                    sp_sub_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                    {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                        {


                                            if (position==0)
                                            {
                                                serviceID="";
                                                Log.e("Serviceid ",serviceID);
                                            }
                                            else
                                             {
                                                serviceID=listOthersd.get(position-1).getId()+"";
                                                Log.e("Serviceid ",serviceID);
                                            }



                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent)
                                        {

                                        }
                                    });
    }

    ArrayList<MatrixServices> listMatrix=new ArrayList<>();
    ArrayList<String> matrixName=new ArrayList<>();

    ArrayList<OtherServices> listOthersd=new ArrayList<>();
    ArrayList<String> otherName=new ArrayList<>();
}
