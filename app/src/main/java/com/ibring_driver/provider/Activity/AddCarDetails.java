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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.AppHelper;
import com.ibring_driver.provider.Helper.ConnectionHelper;
import com.ibring_driver.provider.Helper.CustomDialog;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.Models.BrandList;
import com.ibring_driver.provider.Models.CabType;
import com.ibring_driver.provider.Models.CapacityList;
import com.ibring_driver.provider.Models.MatrixServices;
import com.ibring_driver.provider.Models.ModelList;
import com.ibring_driver.provider.Models.OtherServices;
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
import static com.ibring_driver.provider.XuberServicesApplication.trimMessage;

public class AddCarDetails extends AppCompatActivity implements RetrofitResponse
{

    public Context context = AddCarDetails.this;
    public Activity activity = AddCarDetails.this;
    String TAG = "AddCarDetails";
    String device_token, device_UDID;
    ImageView backArrow;
    CustomDialog customDialog;
    ConnectionHelper helper;
    String key = "";
    String soacialId = "";
    Boolean isInternet;
    Utilities utils = new Utilities();
    XuberServicesApplication xuberServicesApplication;
    public Spinner SpCabType, SpBrand, SpModel, SpCapacity, SpColor;
    RelativeLayout lnrRegister;
    EditText etNumber;
    ImageView ivBack;
    ConstraintLayout cc_food;
    ConstraintLayout ccServices;
    ConstraintLayout cc_carDetails;
    String serviceIdOldScreen="";

    String[] color = {"Select Color", "Red", "Blue", "Black", "Pink", "Brown", "White", "Other"};
    String[] capacityListSpinner = {"Select Capacity", "4", "5", "6", "7"};

    String[] modelllll = {"Select Model"};
    String[] cabType = {"Select Car Type"};
    String[] capacity = {"Select Capacity"};

    ArrayList<CabType> cablist = new ArrayList<>();
    ArrayList<String> cablistName = new ArrayList<>();

    ArrayList<BrandList> brandsList = new ArrayList<>();
    ArrayList<String> brandListName = new ArrayList<>();

    ArrayList<ModelList> modelsList = new ArrayList<>();
    ArrayList<String> modelListName = new ArrayList<>();

    ArrayList<CapacityList> capacityList = new ArrayList<>();
    ArrayList<String> capacityListName = new ArrayList<>();

    RelativeLayout rlService, rlSubService;
    Spinner sp_service, sp_sub_service;

    TextView tvLogin11;

    EditText etVehicleName,etVehicleNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           */

            /*
           getWindow().setStatusBarColor(ContextCompat.getColor(AddCarDetails.this, R.color.colorPrimary));
           getWindow().setNavigationBarColor(ContextCompat.getColor(AddCarDetails.this, R.color.colorPrimary));
           */

        }

        setContentView(R.layout.add_car_details);

        rlService = (RelativeLayout) findViewById(R.id.rlService);
        rlSubService = (RelativeLayout) findViewById(R.id.rlSubService);
        sp_service = (Spinner) findViewById(R.id.sp_service);
        sp_sub_service = (Spinner) findViewById(R.id.sp_sub_service);
        etVehicleName = (EditText) findViewById(R.id.etVehicleName);
        etVehicleNumber = (EditText) findViewById(R.id.etVehicleNumber);

        findViewById();


        if (getIntent().hasExtra("key"))
        {
            key = getIntent().getExtras().getString("key");
            serviceIdOldScreen = getIntent().getExtras().getString("service");

            if (key.equalsIgnoreCase("direct"))
            {
                ccServices.setVisibility(View.GONE);

                if (serviceIdOldScreen.equalsIgnoreCase("18"))
                {
                    cc_food.setVisibility(View.VISIBLE);
                    cc_carDetails.setVisibility(View.GONE);
                }
                else
                {
                    cc_food.setVisibility(View.GONE);
                    cc_carDetails.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                //todo social logins
                soacialId = getIntent().getExtras().getString("social_id");
                ccServices.setVisibility(View.VISIBLE);
                callServiceList();
             }
        }

        xuberServicesApplication = (XuberServicesApplication) getApplication();

        Log.e("Fname ", xuberServicesApplication.signupData.getFname());
        Log.e("Lname ", xuberServicesApplication.signupData.getLname());
        Log.e("Email ", xuberServicesApplication.signupData.getEmail());
        Log.e("Password ", xuberServicesApplication.signupData.getPassword());
        Log.e("Phone ", xuberServicesApplication.signupData.getPhone());

        GetToken();
        setCarCapacity();
        setColor();
        callcabType();
        callbRANDlIST();
    }

    public void callServiceList()
    {
        new RetrofitService(this, this, URLHelper.GET_APP_SERVICES,
                105, 1, "1").callService(true);
    }

    public void GetToken()
    {
        try
        {
            if (!SharedHelper.getKey(context, "device_token").equals("") &&
                    SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.e("@#@#@", "device token" + device_token);
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
            Log.e("@#@#@", "device uid" + device_UDID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        }
        catch (Exception e)
        {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }


    public void GoToMainActivity()
    {
        Intent mainIntent = new Intent(AddCarDetails.this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        AddCarDetails.this.finish();
    }


    public void GoToMainActivity1()
    {
        Intent mainIntent = new Intent(AddCarDetails.this, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        AddCarDetails.this.finish();
    }

    public void setColor() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, color);
        SpColor.setAdapter(adapter);


        SpColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    colorSelect = "";
                    Log.e("colorSelect ", colorSelect);
                } else {
                    colorSelect = SpColor.getSelectedItem() + "";
                    Log.e("colorSelect ", colorSelect);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setCarCapacity()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, capacityListSpinner);
        SpCapacity.setAdapter(adapter);

        SpCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    capacityId = "";
                    Log.e("colorSelect ", capacityId);
                } else {
                    capacityId = SpCapacity.getSelectedItem() + "";
                    Log.e("colorSelect ", capacityId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void callcabType()
    {
        new RetrofitService(this, this, URLHelper.GET_CAB_TYPE,
                500, 1, "1").callService(true);
    }

    public void callbRANDlIST()
    {
        new RetrofitService(this, this, URLHelper.GET_BRAND_LIST,
                1000, 1, "1").callService(true);
    }

    public void callCapacityList(String modelIddd) {
        try {

            JSONObject param = new JSONObject();
            param.put("model_id", modelIddd);

            Log.e("callCapacityListModel ", param.toString());
            new RetrofitService(this, this,
                    URLHelper.GET_CAPACITY_LIST, param, 2000, 2, "1").
                    callService(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void callModelList(String brandId)
    {
        try
        {

            JSONObject param = new JSONObject();
            param.put("brand_id", brandId);
            Log.e("callModelList ", param.toString());
            new RetrofitService(this, this,
                    URLHelper.GET_MODEL_LIST, param, 1500, 2, "1").
                    callService(true);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void callRegisterrrr()
    {
        try
        {
            JSONObject object=new JSONObject();

                object.put("device_type", "android");
            object.put("device_id", device_UDID);

            String serviceIddd="";

            if (key.equalsIgnoreCase("direct"))
            {
                serviceIddd= xuberServicesApplication.signupData.getService_id();
                object.put("service_id", xuberServicesApplication.signupData.getService_id());
                object.put("service_type", xuberServicesApplication.signupData.getService_type());
            }
            else
            {
                serviceIddd=serviceID;
                object.put("service_id", serviceID);
                object.put("service_type", serviceType);
                object.put("social_unique_id", soacialId);
                object.put("login_by", key);
            }


            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");


            //for food module
            if (serviceIddd.equalsIgnoreCase("18"))
            {
                object.put("vehicle_number", etVehicleNumber.getText().toString().trim());
                object.put("vehicle_name", etVehicleName.getText().toString().trim());
            }
            else {
                object.put("car_number", etNumber.getText().toString().trim());
                object.put("car_brand", brandId);
                object.put("car_model", modelId);
                object.put("car_capacity", capacityId);
                object.put("car_color", colorSelect);
                object.put("cab_type_id", cabTypeID);
            }


            object.put("first_name", xuberServicesApplication.signupData.getFname());
            object.put("last_name", xuberServicesApplication.signupData.getLname());
            object.put("email", xuberServicesApplication.signupData.getEmail());
            object.put("password", xuberServicesApplication.signupData.getPassword());
            object.put("password_confirmation", xuberServicesApplication.signupData.getPassword());
            object.put("mobile", xuberServicesApplication.signupData.getPhone());


            Log.e("registerrrrParams ", "" + object);

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
        customDialog = new CustomDialog(AddCarDetails.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);

            if (key.equalsIgnoreCase("direct"))
            {
                object.put("service_id", xuberServicesApplication.signupData.getService_type());
            }
            else
            {
                object.put("service_id", serviceID);
                object.put("social_unique_id", soacialId);
                object.put("login_by", key);
            }

            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", xuberServicesApplication.signupData.getFname());
            object.put("last_name", xuberServicesApplication.signupData.getLname());
            object.put("email", xuberServicesApplication.signupData.getEmail());
            object.put("password", xuberServicesApplication.signupData.getPassword());
            object.put("password_confirmation", xuberServicesApplication.signupData.getPassword());
            object.put("mobile", xuberServicesApplication.signupData.getPhone());
            object.put("car_number", etNumber.getText().toString().trim());
            object.put("car_brand", brandId);
            object.put("car_model", modelId);
            object.put("car_capacity", capacityId);
            object.put("car_color", colorSelect);
            object.put("cab_type_id", cabTypeID);
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
                        SharedHelper.putKey(AddCarDetails.this, "email", xuberServicesApplication.signupData.getEmail());
                        SharedHelper.putKey(AddCarDetails.this, "password", xuberServicesApplication.signupData.getPassword());
                        signIn();
                    }
                },
                new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                utils.print("InsideError ", "" + error);
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try
                            {
//                                displayMessage(errorObj.optString("message"));
                                Toast.makeText(AddCarDetails.this, errorObj.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e)
                            {
//                                displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.statusCode == 401)
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
                                    Toast.makeText(AddCarDetails.this, errorObj.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
//                                displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (response.statusCode == 422)
                        {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null)
                            {
//                                displayMessage(json);
                                Toast.makeText(AddCarDetails.this, json, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                      else
                      {
//                            displayMessage(getString(R.string.please_try_again));
                            Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again),
                                    Toast.LENGTH_SHORT).show();

                     }
                    }
                    catch (Exception e)
                    {
//                        displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong),
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
                GoToMainActivity1();
            }
        },3000);

        alertDialog.show();
    }

    public void signIn() {
        customDialog = new CustomDialog(AddCarDetails.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "password");
            object.put("client_id", URLHelper.CLIENT_ID);
            object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", device_token);
            object.put("email", SharedHelper.getKey(AddCarDetails.this, "email"));
            object.put("password", SharedHelper.getKey(AddCarDetails.this, "password"));
            object.put("scope", "");
            utils.print("InputToLoginAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customDialog.dismiss();
                        utils.print("SignUpResponse", response.toString());
                        SharedHelper.putKey(context, "settings", "no");
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
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
//                                    displayMessage(errorObj.optString("message"));
                                Toast.makeText(AddCarDetails.this, errorObj.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
//                                        displayMessage(errorObj.optString("message"));
                                    Toast.makeText(AddCarDetails.this, errorObj.optString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                    displayMessage(json);
                                Toast.makeText(AddCarDetails.this, json, Toast.LENGTH_SHORT).show();
                            } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                                displayMessage(getString(R.string.please_try_again));
                            Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong),
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
    }

    public void getProfile()
    {
        customDialog = new CustomDialog(AddCarDetails.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.PROVIDER_PROFILE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                utils.print("GetProfile", response.toString());
                SharedHelper.putKey(AddCarDetails.this, "id", response.optString("id"));
                SharedHelper.putKey(AddCarDetails.this, "first_name", response.optString("first_name"));
                SharedHelper.putKey(AddCarDetails.this, "last_name", response.optString("last_name"));
                SharedHelper.putKey(AddCarDetails.this, "email", response.optString("email"));
                SharedHelper.putKey(context, "description", response.optString("description"));
                SharedHelper.putKey(AddCarDetails.this, "picture", AppHelper.getImageUrl(response.optString("picture")));
                SharedHelper.putKey(AddCarDetails.this, "gender", response.optString("gender"));
                SharedHelper.putKey(AddCarDetails.this, "mobile", response.optString("mobile"));
                SharedHelper.putKey(AddCarDetails.this, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(AddCarDetails.this, "payment_mode", response.optString("payment_mode"));
                if (!response.optString("currency").equalsIgnoreCase("") || !response.optString("currency").equalsIgnoreCase("null")) {
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                } else {
                    SharedHelper.putKey(context, "currency", "$");
                }
                SharedHelper.putKey(AddCarDetails.this, "loggedIn", getString(R.string.True));
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
                                Toast.makeText(AddCarDetails.this, errorObj.optString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
//                                        displayMessage(errorObj.optString("message"));
                                    Toast.makeText(AddCarDetails.this, errorObj.optString("message"), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                            }

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                    displayMessage(json);
                                Toast.makeText(AddCarDetails.this, json, Toast.LENGTH_SHORT).show();

                            } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                            }

                        } else {
//                                displayMessage(getString(R.string.please_try_again));
                            Toast.makeText(AddCarDetails.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(AddCarDetails.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(AddCarDetails.this, KeyHelper.KEY_ACCESS_TOKEN));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void findViewById() {
        SpCabType = (Spinner) findViewById(R.id.SpCabType);
        SpBrand = (Spinner) findViewById(R.id.SpBrand);
        SpModel = (Spinner) findViewById(R.id.SpModel);
        SpCapacity = (Spinner) findViewById(R.id.SpCapacity);
        SpColor = (Spinner) findViewById(R.id.SpColor);
        lnrRegister = (RelativeLayout) findViewById(R.id.lnrRegister);
        etNumber = (EditText) findViewById(R.id.etNumber);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvLogin11 = (TextView) findViewById(R.id.tvLogin11);
        cc_food = (ConstraintLayout) findViewById(R.id.cc_food);
        ccServices = (ConstraintLayout) findViewById(R.id.ccServices);
        cc_carDetails = (ConstraintLayout) findViewById(R.id.cc_carDetails);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        tvLogin11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddCarDetails.this, ActivityPassword.class);
                startActivity(intent);
            }
        });


        ArrayAdapter<String> adapter11 = new ArrayAdapter<String>(this, R.layout.custom_spinner, cabType);
        SpCabType.setAdapter(adapter11);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.custom_spinner, modelllll);
        SpModel.setAdapter(adapter);


        ArrayAdapter<String> adapter23 = new ArrayAdapter<String>(this, R.layout.custom_spinner, capacity);
        SpCapacity.setAdapter(adapter23);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        lnrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeypad(AddCarDetails.this, lnrRegister);
                if (checkValidations())
                {
//                    registerAPI();
                    callRegisterrrr();
                }
            }
        });
    }

    public boolean checkValidations() {

        if (key.equalsIgnoreCase("direct"))
        {
            if (serviceIdOldScreen.equalsIgnoreCase("18"))
            {
                if (etVehicleName.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(this, "Vehicle Name  can't be empty", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (etVehicleNumber.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(this, "Vehicle Number  can't be empty", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                if (cabTypeID.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose a Cab Type", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (etNumber.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(this, "Plate number can't be empty", Toast.LENGTH_SHORT).show();
                    return false;
                }  else if (etNumber.getText().toString().length()<5)
                {
                    Toast.makeText(this, "Plate number must be atleast 5 digits long", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (brandId.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose your car brand", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (modelId.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Choose the model of your car", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (capacityId.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose capacity", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (colorSelect.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please Choose your car color", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }



        return true;
    }

    @Override
    public void onResponse(int RequestCode, String response) {
        switch (RequestCode) {
            case 500:
                Log.e("CabTYpeList ", response);
                try {
                    cablist.clear();
                    cablistName.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    Log.e("jsonArrayLength ", jsonArray.length() + "");

                    cablistName.add("Select Cab Type");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject cabJson = jsonArray.getJSONObject(i);
                            CabType cabType = new CabType();
                            cabType.setId(cabJson.getString("id"));
                            cabType.setName(cabJson.getString("cab_name"));
                            cabType.setPrice(cabJson.getString("price"));
                            cabType.setImage(cabJson.getString("image"));
                            cablist.add(cabType);
                            cablistName.add(cabJson.getString("cab_name"));
                        }
                    }

                    Log.e("cablist ", cablist.size() + "");
                    Log.e("CabNameListSize ", cablistName.size() + "");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, cablistName);
                    SpCabType.setAdapter(adapter);


                    SpCabType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            if (position == 0)
                            {
                                cabTypeID = "";
                                Log.e("cabTypeID ", cabTypeID);
                            } else
                            {
                                cabTypeID = cablist.get(position - 1).getId() + "";
                                Log.e("cabTypeID ", cabTypeID);
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

            case 1000:
                Log.e("BrandList ", response);
                try {
                    brandsList.clear();
                    brandListName.clear();

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        Log.e("jsonArrayLength ", jsonArray.length() + "");

                        brandListName.add("Select Brand");

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject cabJson = jsonArray.getJSONObject(i);


                                BrandList brandList = new BrandList();

                                brandList.setId(cabJson.getString("id"));
                                brandList.setBrandName(cabJson.getString("brand_name"));

                                brandsList.add(brandList);
                                brandListName.add(cabJson.getString("brand_name"));
                            }
                        }

                        Log.e("brandsList ", brandsList.size() + "");
                        Log.e("brandListName ", brandListName.size() + "");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, brandListName);
                        SpBrand.setAdapter(adapter);


                        SpBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    brandId = "";
                                    Log.e("brandId ", brandId);
                                } else {
                                    brandId = brandsList.get(position - 1).getId() + "";
                                    Log.e("brandId ", brandId);
                                    callModelList(brandId);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


            case 1500:
                Log.e("ModelList ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        modelsList.clear();
                        modelListName.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        Log.e("jsonArrayLength ", jsonArray.length() + "");

                        modelListName.add("Select Model");

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject cabJson = jsonArray.getJSONObject(i);


                                ModelList modelList = new ModelList();

                                modelList.setId(cabJson.getString("id"));
                                modelList.setName(cabJson.getString("model_name"));

                                modelsList.add(modelList);
                                modelListName.add(cabJson.getString("model_name"));
                            }
                        }

                        Log.e("modelsList ", modelsList.size() + "");
                        Log.e("modelListName ", modelListName.size() + "");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                R.layout.custom_spinner, modelListName);
                        SpModel.setAdapter(adapter);


                        SpModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    modelId = "";
                                    Log.e("modelId ", modelId);
                                } else {
                                    modelId = modelsList.get(position - 1).getId() + "";
                                    Log.e("modelId ", modelId);
//                                    callCapacityList(modelId);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 2000:
                Log.e("capacityList ", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        capacityList.clear();
                        capacityListName.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        Log.e("jsonArrayLength ", jsonArray.length() + "");

                        capacityListName.add("Select Capacity");

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject cabJson = jsonArray.getJSONObject(i);


                                CapacityList list = new CapacityList();

                                list.setId(cabJson.getString("id"));
                                list.setName(cabJson.getString("cc_name"));

                                capacityList.add(list);
                                capacityListName.add(cabJson.getString("cc_name"));
                            }
                        }

                        Log.e("capacityList ", capacityList.size() + "");
                        Log.e("capacityListName ", capacityListName.size() + "");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (this, R.layout.custom_spinner, capacityListName);
                        SpCapacity.setAdapter(adapter);


                        SpCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    capacityId = "";
                                    Log.e("capacityId ", capacityId);
                                } else {
                                    capacityId = capacityList.get(position - 1).getId() + "";
                                    Log.e("capacityId ", capacityId);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 105:
                Log.e("ResponseServiceList ", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("jsonObject ", jsonObject.toString());

                    listMatrix.clear();
                    matrixName.clear();
                    listOthersd.clear();
                    otherName.clear();

                    matrixName.add("Choose a Service");
                    otherName.add("Choose  Other Services");

                    JSONArray matrix = jsonObject.getJSONArray("matrix");

                    if (matrix.length() > 0) {
                        for (int i = 0; i < matrix.length(); i++) {
                            MatrixServices matrixServices = new MatrixServices();
                            JSONObject jsonObject1 = matrix.getJSONObject(i);

                            matrixServices.setId(jsonObject1.getString("id"));
                            matrixServices.setName(jsonObject1.getString("service_types"));

                            listMatrix.add(matrixServices);
//                            matrixName.add(jsonObject1.getString("service_types"));
                            matrixName.add(jsonObject1.getString("name"));
                        }
                    }

                    JSONArray others = jsonObject.getJSONArray("others");

                    if (others.length() > 0) {
                        for (int j = 0; j < others.length(); j++) {
                            OtherServices other = new OtherServices();
                            JSONObject jsonObject1 = others.getJSONObject(j);

                            other.setId(jsonObject1.getString("id"));
                            other.setName(jsonObject1.getString("name"));

                            listOthersd.add(other);
                            otherName.add(jsonObject1.getString("name"));
                        }
                    }

                    matrixName.add(4, "Others");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, matrixName);
                    sp_service.setAdapter(adapter);

                    final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                            this, R.layout.custom_spinner, matrixName) {
                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (position == 0) {
                                // Set the item text color
                                tv.setTextColor(Color.parseColor("#091046"));
                            } else {
                                // Set the alternate item text color
                                tv.setTextColor(Color.parseColor("#091046"));
                            }
                            return view;
                        }
                    };

                    spinnerArrayAdapter1.setDropDownViewResource(R.layout.custom_spinner);
                    sp_service.setAdapter(spinnerArrayAdapter1);

                    sp_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if (position == matrixName.size() - 1)
                            {
                                serviceID = "";
                                sp_sub_service.setVisibility(View.VISIBLE);
                                rlSubService.setVisibility(View.VISIBLE);
                                Log.e("Serviceid ", serviceID);
                                Log.e("SubServiceLisdt ", listOthersd.size() + "");
                                Log.e("name ", otherName.size() + "");
                                setSubServiceAdapter();
                            } else {
                                if (position == 0) {
                                    serviceID = "";
                                    Log.e("Serviceid ", serviceID);
                                    serviceType = "";
                                    Log.e("serviceType ", serviceType);
                                } else {
                                    serviceID = listMatrix.get(position - 1).getId() + "";
                                    Log.e("Serviceid ", serviceID);
                                    serviceType = listMatrix.get(position - 1).getName() + "";
                                    Log.e("serviceType ", serviceType);
                                    sp_sub_service.setVisibility(View.GONE);
                                    rlSubService.setVisibility(View.GONE);

                                }

                                if (key.equalsIgnoreCase("google"))
                                {
                                    if (serviceID.equalsIgnoreCase("18"))
                                    {
                                        cc_food.setVisibility(View.VISIBLE);
                                        cc_carDetails.setVisibility(View.GONE);
                                    } else if (serviceID.equalsIgnoreCase("19"))
                                    {
                                        cc_food.setVisibility(View.GONE);
                                        cc_carDetails.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        cc_food.setVisibility(View.GONE);
                                        cc_carDetails.setVisibility(View.VISIBLE);
                                    }
                                }else if (key.equalsIgnoreCase("facebook"))
                                {
                                    if (serviceID.equalsIgnoreCase("18"))
                                    {
                                        cc_food.setVisibility(View.VISIBLE);
                                        cc_carDetails.setVisibility(View.GONE);
                                    }
                                    else if (serviceID.equalsIgnoreCase("19"))
                                    {
                                        cc_food.setVisibility(View.GONE);
                                        cc_carDetails.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        cc_food.setVisibility(View.GONE);
                                        cc_carDetails.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    });


                } catch (Exception ex) {
                    ex.printStackTrace();
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
                        else
                        {
                            showVerificationPopUp();
                        }
                    }
                    else
                    {
                        if (jsonObject.has("error"))
                        {
                            Toast.makeText(this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    }

                   /*
                   else {
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
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }

    String cabTypeID = "";
    String brandId = "";
    String modelId = "";
    String capacityId = "";
    String colorSelect = "";

    public void setSubServiceAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                AddCarDetails.this, R.layout.custom_spinner, otherName);

        sp_sub_service.setAdapter(adapter);


        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                AddCarDetails.this,R.layout.custom_spinner,otherName)
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

    String serviceID="";
    String serviceType="";

}
