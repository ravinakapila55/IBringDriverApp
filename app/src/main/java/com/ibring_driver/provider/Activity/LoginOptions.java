package com.ibring_driver.provider.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.Models.SignupData;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.XuberServicesApplication;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginOptions extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rlEmail;
    RelativeLayout rlFb;
    RelativeLayout rlGoogle;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_option);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("printHashKey() Hash Key: " , hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e( "printHashKey()", e.getMessage());
        } catch (Exception e) {
            Log.e( "printHashKey()", e.toString());
        }



        callbackManager=CallbackManager.Factory.create();
        rlEmail=(RelativeLayout)findViewById(R.id.rlEmail);
        rlFb=(RelativeLayout)findViewById(R.id.rlFb);
        rlGoogle=(RelativeLayout)findViewById(R.id.rlGoogle);
        rlEmail.setOnClickListener(this);
        rlFb.setOnClickListener(this);
        rlGoogle.setOnClickListener(this);

        xuberServicesApplication=(XuberServicesApplication)getApplication();
        if (getIntent().getIntExtra("ADD_NEW",0)==0)
        {
            xuberServicesApplication.signupData=new SignupData();
        }

/*
        String keyHash="";
        keyHash=generateKeyHash();

        Log.e("KeyHash ",keyHash);
        Toast.makeText(this, "KeyHash "+keyHash, Toast.LENGTH_SHORT).show();*/


    }

    CallbackManager callbackManager=null;


    private String generateKeyHash()
    {
        try {
        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                PackageManager.GET_SIGNATURES); for (Signature signature : info.signatures)
                { MessageDigest md = (MessageDigest.getInstance("SHA")); md.update(signature.toByteArray());
                return new String(Base64.encode(md.digest(), 0)); } }
                catch (Exception e)
                {
                    Log.e("exception", e.toString());
    }
    return "key hash not found";
    }



    public void fblogin() {


        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("public_profile", "email")
        );

        Log.e("callbackManager ",callbackManager.toString());
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {

                        Log.e("FbLoginSuccess ", loginResult.toString());
                        Log.e("FbLoginSuccessAccess ", loginResult.getAccessToken().getApplicationId());
                        Log.e("FbLoginSuccessAccess ", loginResult.getAccessToken().getUserId());
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Log.e("JSONObject Value ",object.toString());
                                    socialLoginId=object.getString("id");
                                    /* registerAPI("facebook",object.getString("email"),object.getString("first_name"),
                                            object.getString("last_name"),"",object.getString("id"));*/
                                    xuberServicesApplication.signupData.setFname(object.getString("first_name"));
                                    xuberServicesApplication.signupData.setLname(object.getString("last_name"));
                                    xuberServicesApplication.signupData.setEmail(object.getString("email"));
                                    xuberServicesApplication.signupData.setPassword("123456");
                                    xuberServicesApplication.signupData.setCpassword("123456");
                                    xuberServicesApplication.signupData.setPhone("4563245674");

                                    Intent intent=new Intent(LoginOptions.this,AddCarDetails.class);
                                    intent.putExtra("key","facebook");
                                    intent.putExtra("social_id",socialLoginId);
                                    startActivity(intent);
                                    LoginManager.getInstance().logOut();
                                } catch (Exception e) {
                                }
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "email,id,first_name,last_name,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("onCancel ", "cancel");

                    }


                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("onCancel ", "cancel");
                    }
                }
        );

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.rlEmail:

                SharedHelper.putKey(this, "loggedIn", "false");
                Intent mainIntent = new Intent(this, ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);

                break;

            case R.id.rlFb:
                Log.e("FbClick  ","Click");


                try {
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken!=null){
                        LoginManager.getInstance().logOut();
                    }

                    fblogin();

                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }


                 break;

            case R.id.rlGoogle:
                googleSignIn();
                break;
        }

    }


    GoogleSignInClient mGoogleSignInClient;
    public void googleSignIn()
    {
        Log.e("insideGoogleSignIn ","googleSignin");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GooglesignIn();
    }

    private void GooglesignIn() {
        Log.e("insideSendAcrivit ","yes");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1000)
        {
            Log.e("inside 1000 ","insideOnActivityResult");

            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else {
            Log.e("inside OnActivity ",data.toString());
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    String socialLoginId="";
    XuberServicesApplication xuberServicesApplication;
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e( "Email " ,account.getEmail());
            Log.e( "Idd ",account.getId());
            Log.e( "DisplayName " ,account.getDisplayName());
            Log.e( "GivenName ", account.getGivenName());
//          Log.e(TAG, "signInResult:account Success" + account.getPhotoUrl());
            socialLoginId=account.getId();
            xuberServicesApplication.signupData.setFname(account.getDisplayName());
            xuberServicesApplication.signupData.setLname(account.getGivenName());
            xuberServicesApplication.signupData.setEmail(account.getEmail());
            xuberServicesApplication.signupData.setPassword("123456");
            xuberServicesApplication.signupData.setCpassword("123456");
            xuberServicesApplication.signupData.setPhone("4563245674");
        /*    xuberServicesApplication.signupData.setSer("15");
            xuberServicesApplication.signupData.setService_type("15");*/

            Intent intent=new Intent(LoginOptions.this,AddCarDetails.class);
            intent.putExtra("key","google");
            intent.putExtra("social_id",socialLoginId);
            startActivity(intent);
           // registerAPI("google",account.getEmail(),account.getDisplayName(),account.getGivenName(),"",account.getId());
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e( "signInResult: " , e.getStatusCode()+"");
//          updateUI(null);
        }
    }
}
