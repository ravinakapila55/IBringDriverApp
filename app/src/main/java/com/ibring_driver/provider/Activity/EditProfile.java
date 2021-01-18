package com.ibring_driver.provider.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.AppHelper;
import com.ibring_driver.provider.Helper.ConnectionHelper;
import com.ibring_driver.provider.Helper.CustomDialog;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.Helper.VolleyMultipartRequest;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ibring_driver.provider.XuberServicesApplication.trimMessage;

public class EditProfile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    String TAG = "EditActivity";
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView changePasswordTxt, lblSave, email;
    EditText first_name, last_name, mobile_no, desc;
    ImageView profile_Image;
    Boolean isImageChanged = false;
    TextView tvMore;

    ImageView backImg;
    Uri filePath;
    ConstraintLayout ccMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_edit_profile);

        getProfile();

        findViewByIdandInitialization();

        enableDisableETxt(false);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        backImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        lblSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblSave.getText().toString().equalsIgnoreCase(getString(R.string.edit))) {
                    lblSave.setText(getString(R.string.save));
                    enableDisableETxt(true);
                } else
                    {

                    Pattern ps = Pattern.compile(".*[0-9].*");
                    Matcher firstName = ps.matcher(first_name.getText().toString());
                    Matcher lastName = ps.matcher(last_name.getText().toString());

                    if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
//                        displayMessage(getString(R.string.email_validation));
                        Toast.makeText(EditProfile.this,getString(R.string.email_validation), Toast.LENGTH_SHORT).show();

                    } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
//                        displayMessage(getString(R.string.mobile_number_empty));
                        Toast.makeText(EditProfile.this,getString(R.string.mobile_number_empty), Toast.LENGTH_SHORT).show();

                        Toast.makeText(EditProfile.this,getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();

                    } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
//                        displayMessage(getString(R.string.first_name_empty));
                        Toast.makeText(EditProfile.this,getString(R.string.first_name_empty), Toast.LENGTH_SHORT).show();

                    } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
//                        displayMessage(getString(R.string.last_name_empty));
                        Toast.makeText(EditProfile.this,getString(R.string.last_name_empty), Toast.LENGTH_SHORT).show();

                    } else if (firstName.matches()) {
//                        displayMessage(getString(R.string.first_name_no_number));
                        Toast.makeText(EditProfile.this,getString(R.string.first_name_no_number), Toast.LENGTH_SHORT).show();

                    } else if (lastName.matches()) {
                        displayMessage(getString(R.string.last_name_no_number));
                        Toast.makeText(EditProfile.this,getString(R.string.last_name_no_number), Toast.LENGTH_SHORT).show();

                        Toast.makeText(EditProfile.this,getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();

                    }else if(mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20){
//                        displayMessage(getString(R.string.mobile_no_validation));
                        Toast.makeText(EditProfile.this,getString(R.string.mobile_no_validation), Toast.LENGTH_SHORT).show();

                    } else {
                        if (isInternet) {
                            updateProfile();
                        } else {
//                            displayMessage(getString(R.string.something_went_wrong_net));
                            Toast.makeText(EditProfile.this,getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });

        profile_Image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: ProfilePic");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(checkStoragePermission()) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                            }else{
                                goToImageIntent();

                            }
                        }else{
                            goToImageIntent();

                        }

            }
        });


    }

    public void getProfile()
    {
        customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            Log.e("ProfileUrl ", URLHelper.PROVIDER_PROFILE);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URLHelper.PROVIDER_PROFILE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    customDialog.dismiss();
                    Log.e("GetProfileResponse ", response.toString());
                    try {
                        JSONObject service=response.getJSONObject("service");
                        JSONObject service_type=service .getJSONObject("service_type");
                        Log.e("ServiceTypevlaue ",service_type.getString("service_types"));
                        SharedHelper.putKey(context, "service_types", service_type.getString("service_types"));

                        serviceType=service_type.getString("service_types");

                        if (serviceType.equalsIgnoreCase("taxi_service"))
                        {
                            JSONObject cab_detail=service.getJSONObject("cab_detail");
                            JSONObject cab_type1=cab_detail.getJSONObject("cab_type");

                            cab_type=cab_type1.getString("cab_name");
                            car_name=cab_detail.getString("car_model");
                            car_color=cab_detail.getString("car_color");
                            car_brand=cab_detail.getString("car_brand");
                            car_number=cab_detail.getString("car_number");

                            CC_CabDetails.setVisibility(View.VISIBLE);
                            CC_VehicleDetails.setVisibility(View.GONE);

                            tvCabType.setText(cab_type);
                            tvServiceType.setText(serviceType);
                            tvCarName.setText(car_name);
                            tvCarNumber.setText(car_number);
                            tvCarBrand.setText(car_brand);
                            tvCarColor.setText(car_color);



                        }

                        else
                        {
                            CC_CabDetails.setVisibility(View.GONE);
                            CC_VehicleDetails.setVisibility(View.VISIBLE);
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
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
//                                    displayMessage(errorObj.optString("message"));
                                    Toast.makeText(EditProfile.this, errorObj.optString("message").toString(), Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
                                    Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                                }
                            } else if (response.statusCode == 401) {
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    displayMessage(json);
                                    Toast.makeText(EditProfile.this, json.toString(), Toast.LENGTH_SHORT).show();

                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
                                    Toast.makeText(EditProfile.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                                }

                            } else if (response.statusCode == 503) {
//                                displayMessage(getString(R.string.server_down));
                                Toast.makeText(EditProfile.this, getString(R.string.server_down), Toast.LENGTH_SHORT).show();

                            } else {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(EditProfile.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
                            Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                    Log.e("AuthorizationValue ","Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = EditProfile.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_change_password, null);

        builder.setView(view);

        final EditText currentPassword = (EditText) view.findViewById(R.id.current_password);
        final EditText newPassword = (EditText) view.findViewById(R.id.new_password);
        final EditText confirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        final ImageView ivCurrentPswd = (ImageView) view.findViewById(R.id.ivCurrentPswd);
        final ImageView ivNewPswd = (ImageView) view.findViewById(R.id.ivNewPswd);
        final ImageView ivConfirmPswd = (ImageView) view.findViewById(R.id.ivConfirmPswd);

        Button cancelBtn = (Button) view.findViewById(R.id.btnCancel);
        Button submit = (Button) view.findViewById(R.id.changePasswordBtn);




        currentPassword.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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
        newPassword.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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
        confirmPassword.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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


        ivCurrentPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    ivCurrentPswd.setImageResource(R.drawable.hide_eye);
                    currentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    currentPassword.setSelection(currentPassword.length());
                }
                else {
                    ivCurrentPswd.setImageResource(R.drawable.visible_eye);
                    currentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    currentPassword.setSelection(currentPassword.length());
                }
            }
        });


        ivNewPswd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (newPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    ivNewPswd.setImageResource(R.drawable.hide_eye);
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPassword.setSelection(newPassword.length());

                }
                else {
                    ivNewPswd.setImageResource(R.drawable.visible_eye);
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setSelection(newPassword.length());

                }
            }
        });

        ivConfirmPswd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (confirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    ivConfirmPswd.setImageResource(R.drawable.hide_eye);
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPassword.setSelection(confirmPassword.length());

                }
                else {
                    ivConfirmPswd.setImageResource(R.drawable.visible_eye);
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword.setSelection(confirmPassword.length());

                }
            }
        });



        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPassword.getText().toString().length() == 0) {
//                    displayMessage(getString(R.string.please_enter_current_pass));
                    Toast.makeText(EditProfile.this,getString(R.string.please_enter_current_pass), Toast.LENGTH_SHORT).show();

                } else if (newPassword.getText().toString().length() == 0) {
//                    displayMessage(getString(R.string.please_enter_new_pass));
                    Toast.makeText(EditProfile.this,getString(R.string.please_enter_new_pass), Toast.LENGTH_SHORT).show();

                } else if (confirmPassword.getText().toString().length() == 0) {
//                    displayMessage(getString(R.string.please_enter_confirm_pass));
                    Toast.makeText(EditProfile.this,getString(R.string.please_enter_confirm_pass), Toast.LENGTH_SHORT).show();

                } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
//                    displayMessage(getString(R.string.different_passwords));
                    Toast.makeText(EditProfile.this,getString(R.string.different_passwords), Toast.LENGTH_SHORT).show();

                } else {
                    dialog.dismiss();
                    changePassword(currentPassword.getText().toString(), newPassword.getText().toString(), confirmPassword.getText().toString());
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changePassword(String current_pass, String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("password_old", current_pass);
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
//                displayMessage(response.optString("message"));
                Toast.makeText(EditProfile.this,response.optString("message"), Toast.LENGTH_SHORT).show();

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
                //Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
//                                displayMessage(errorObj.optString("error"));
                                Toast.makeText(EditProfile.this,errorObj.optString("error"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
//                                displayMessage(getString(R.string.something_went_wrong));
                                Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                displayMessage(json);
                                Toast.makeText(EditProfile.this,json, Toast.LENGTH_SHORT).show();

                            } else {
//                                displayMessage(getString(R.string.please_try_again));
                                Toast.makeText(EditProfile.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                            }

                        } else {
//                            displayMessage(getString(R.string.please_try_again));
                            Toast.makeText(EditProfile.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void enableDisableETxt(boolean isEnabled) {
        profile_Image.setEnabled(isEnabled);
        first_name.setEnabled(isEnabled);
        last_name.setEnabled(isEnabled);
        mobile_no.setEnabled(isEnabled);
        desc.setEnabled(isEnabled);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.e(TAG, "onActivityResult: img url" + filePath);
            try {
                Log.e(TAG, "onActivityResult: ");
                isImageChanged = true;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_Image.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Bitmap getScaledBitmap(Bitmap mBitmap){
        int nh = (int) (mBitmap.getHeight() * (512.0 / mBitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(mBitmap, 512, nh, true);
        return scaled;
    }

    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    ConstraintLayout CC_VehicleDetails,CC_CabDetails;
    TextView tvVehicleNumber,tvVehicleName,tvServiceType,tvCabType,tvCarName,tvCarNumber,tvCarBrand,tvCarColor;
    ImageView ivCross;





    public void updateProfileWithImage()
    {
        isImageChanged = false;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.PROVIDER_PROFILE_UPDATE,
                new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    Log.e("ProfileUpdateResponse ",jsonObject.toString());
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                        SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("avatar").equals("") || jsonObject.optString("avatar") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture",
                                AppHelper.getImageUrl(jsonObject.optString("avatar")));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "description", jsonObject.optString("description"));
//                    displayMessage(getString(R.string.update_success));
                    Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    lblSave.setText(getString(R.string.edit));
                    enableDisableETxt(false);
                } catch (JSONException e) {
                    e.printStackTrace();
//                    displayMessage(getString(R.string.something_went_wrong));
                    Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                Log.e("errorrrr ",error.toString());
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        updateProfileWithoutImage();
                    }else{
//                        displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    displayMessage(getString(R.string.something_went_wrong));
                    Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("description", desc.getText().toString());
                if (filePath != null) {
                    params.put("avatar", URLHelper.BASE_URL + "/" + filePath);
                } else {
                    params.put("avatar", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("avatar", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }


    public void updateProfileWithoutImage(){
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                URLHelper.PROVIDER_PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();
                Log.e(TAG, "onResponse: Profile Update" + response.data);
                Log.e(TAG, "onResponse: Profile Update" + response.toString());
                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("avatar").equals("") || jsonObject.optString("avatar") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(jsonObject.optString("avatar")));
                    }
                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "description", jsonObject.optString("description"));

//                    displayMessage(getString(R.string.update_success));
                    Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();

                    lblSave.setText(getString(R.string.edit));
                    enableDisableETxt(false);

                } catch (JSONException e) {
                    e.printStackTrace();
//                    displayMessage(getString(R.string.something_went_wrong));
                    Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        updateProfileWithoutImage();
                    }else{
//                        displayMessage(getString(R.string.something_went_wrong));
                        Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                    }
                }else{
//                    displayMessage(getString(R.string.something_went_wrong));
                    Toast.makeText(EditProfile.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("description", desc.getText().toString());
                if (filePath != null) {
                    params.put("avatar", URLHelper.BASE_URL + "/" + filePath);
                } else {
                    params.put("avatar", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    public void findViewByIdandInitialization()
    {
        email = (TextView) findViewById(R.id.email);
        tvMore = (TextView) findViewById(R.id.tvMore);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        desc = (EditText) findViewById(R.id.desc);
        changePasswordTxt = (TextView) findViewById(R.id.changePasswordTxt);
        profile_Image = (ImageView) findViewById(R.id.img_profile);
        ccMore = (ConstraintLayout) findViewById(R.id.ccMore);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        lblSave = (TextView) findViewById(R.id.lblSave);
        backImg = (ImageView) findViewById(R.id.backArrow);


        ConstraintLayout CC_VehicleDetails=(ConstraintLayout)findViewById(R.id.CC_VehicleDetails);
        ConstraintLayout CC_CabDetails=(ConstraintLayout)findViewById(R.id.CC_CabDetails);

        TextView tvVehicleNumber=(TextView)findViewById(R.id.tvVehicleNumber);
        TextView tvVehicleName=(TextView) findViewById(R.id.tvVehicleName);
        TextView tvServiceType=(TextView) findViewById(R.id.tvServiceType);
        TextView tvCabType=(TextView) findViewById(R.id.tvCabType);
        TextView tvCarName=(TextView) findViewById(R.id.tvCarName);
        TextView tvCarNumber=(TextView)findViewById(R.id.tvCarNumber);
        TextView tvCarBrand=(TextView) findViewById(R.id.tvCarBrand);
        TextView tvCarColor=(TextView) findViewById(R.id.tvCarColor);
        ImageView ivCross=(ImageView)findViewById(R.id.ivCross);

        ivCross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ccMore.isShown())
                {
                    ccMore.setVisibility(View.GONE);
                }
            }
        });

        tvMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (ccMore.isShown())
                {
                    ccMore.setVisibility(View.GONE);

                }
                else {
                    ccMore.setVisibility(View.VISIBLE);
                }
//               callMoreProfileDetail();
            }
        });

        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).
        // placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);

        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase(""))
        {
            Log.e(TAG, "findViewByIdandInitialization: " + SharedHelper.getKey(context, "picture"));
            Picasso.with(context).load(SharedHelper.getKey(context, "picture")).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null"))
        {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }
        String description = SharedHelper.getKey(context, "description");
        if (description != null && !description.equalsIgnoreCase("null") && description.length() > 0)
        {
            desc.setText(description);
        }
    }

    public void callMoreProfileDetail()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.more_profile_detail_popup, null);

        ConstraintLayout CC_VehicleDetails=(ConstraintLayout) dialogView.findViewById(R.id.CC_VehicleDetails);
        ConstraintLayout CC_CabDetails=(ConstraintLayout) dialogView.findViewById(R.id.CC_CabDetails);

        TextView tvVehicleNumber=(TextView) dialogView.findViewById(R.id.tvVehicleNumber);
        TextView tvVehicleName=(TextView) dialogView.findViewById(R.id.tvVehicleName);
        TextView tvServiceType=(TextView) dialogView.findViewById(R.id.tvServiceType);
        TextView tvCabType=(TextView) dialogView.findViewById(R.id.tvCabType);
        TextView tvCarName=(TextView) dialogView.findViewById(R.id.tvCarName);
        TextView tvCarNumber=(TextView) dialogView.findViewById(R.id.tvCarNumber);
        TextView tvCarBrand=(TextView) dialogView.findViewById(R.id.tvCarBrand);
        TextView tvCarColor=(TextView) dialogView.findViewById(R.id.tvCarColor);
        ImageView ivCross=(ImageView) dialogView.findViewById(R.id.ivCross);


        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setGravity(Gravity.CENTER);


        if (serviceType.equalsIgnoreCase("taxi_service"))
        {
            CC_CabDetails.setVisibility(View.VISIBLE);
            CC_VehicleDetails.setVisibility(View.GONE);

            tvCabType.setText(cab_type);
            tvServiceType.setText(serviceType);
            tvCarName.setText(car_name);
            tvCarNumber.setText(car_number);
            tvCarBrand.setText(car_brand);
            tvCarColor.setText(car_color);
        }
        else
        {
            CC_CabDetails.setVisibility(View.GONE);
            CC_VehicleDetails.setVisibility(View.VISIBLE);
        }


        ivCross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    String serviceType="",cab_type="",car_name="",car_number="",car_color="",car_brand="",car_capacity="",vehicle_number,vehicle_name="";

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
     /*   TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));*/
        snackbar.show();
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
    public void onBackPressed() {
        GoToMainActivity();
    }

    public void GoToBeginActivity()
    {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

}
