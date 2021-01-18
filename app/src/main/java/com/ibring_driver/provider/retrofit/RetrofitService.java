package com.ibring_driver.provider.retrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.SharedHelper;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitService
{
    private Context mcontext;
    private String mUrl;
    private int mValue,mRequestCode;
    private HashMap<String, RequestBody> map;
    private HashMap<String, String> map1;
    private RetrofitResponse mResponse;
    private Call<ResponseBody> mCall;
    private MultipartBody.Part mpart;
    private JSONObject mJsonObject;
    private ProgressDialog dialog;
    private ArrayList<MultipartBody.Part> mPartList;
    private Dialog pd;
    String apiCallTYpe="no";
    int apiCall=0;



    //For Get Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse, String mUrl,
                           int mRequestCode, int mValue, String apiCallTYpe)
    {
        Log.e("inside ","1");
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCallTYpe=apiCallTYpe;
    }

    //For Post Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse, String mUrl, JSONObject postparam, int mRequestCode, int mValue, String apiCallTYpe)
    {
        Log.e("inside ","2");

        Log.e("URLLLLL ",mUrl);

        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mJsonObject = postparam;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCallTYpe=apiCallTYpe;
        Log.e("URLLLLL2 ",this.mUrl);

      /*  dialog=new ProgressDialog(mcontext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");*/

    }
    //For Post Request



    public RetrofitService(Context mcontext, RetrofitResponse mResponse,
                           String mUrl, HashMap<String,String> map1, int mRequestCode, int mValue,int apiCall)
    {
        Log.e("inside ","3");

        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.map1 = map1;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCall=apiCall;

      /* dialog=new ProgressDialog(mcontext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");*/
    }

    //For Post Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse, String mUrl,
                           HashMap<String, RequestBody> postparam,
                           int mRequestCode, int mValue, String apiCallTYpe)
    {

        Log.e("inside ","4");
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.map = postparam;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCallTYpe=apiCallTYpe;
        dialog=new ProgressDialog(mcontext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");
    }

    //For Multipart Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse,
                           String mUrl, HashMap<String, RequestBody> map, MultipartBody.Part mpart,
                           int mRequestCode, int mValue)
    {

        Log.e("inside ","5");

        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mpart = mpart;
        this.map = map;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
    }

        //For multipart multiple files
    public RetrofitService(Context mcontext, RetrofitResponse response, String url,
                           int requestCode, int value, HashMap<String, RequestBody> map,
                           ArrayList<MultipartBody.Part> files)
    {
        Log.e("inside ","6");
        this.mcontext = mcontext;
        mResponse = response;
        mUrl = url;
        mRequestCode = requestCode;
        mValue = value;
        this.map = map;
        mPartList = files;
    }

    public RetrofitService()
    {

    }

    public void callService(boolean ProgressDialog)
    {
        try {
            dialog = new ProgressDialog(mcontext);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Loading....");

            if (ProgressDialog)
            {
                dialog.show();
            }

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

//            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .writeTimeout(4,TimeUnit.MINUTES)
            ;

            String token= SharedHelper.getKey(mcontext,"access_token");
            Log.e("Token  ",token);
            final String value="Bearer "+ token;
            Log.e("value  ", value);

            httpClient.addInterceptor(new Interceptor()
            {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException
                {
                    Request request = chain.request().newBuilder().
                            addHeader("Authorization",value).build();

                    Log.e("Requesttt ",request+"");
                    return chain.proceed(request);
                }
            });
            Retrofit retrofit = null;

            System.setProperty("http.keepAlive", "false");

            Log.e("apiCallTYpe ",apiCallTYpe+"");

            if (apiCallTYpe.equalsIgnoreCase("1") )
            {
                Log.e("apiCallTYpe Inside",apiCallTYpe+"");
                Log.e("Baseeee",URLHelper.BASE_URL1+"");
                System.setProperty("http.keepAlive", "false");

                retrofit = new Retrofit.Builder()
                        .baseUrl(URLHelper.BASE_URL1)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }else if (apiCallTYpe.equalsIgnoreCase("8") )
            {
                Log.e("apiCallTYpe Inside",apiCallTYpe+"");
                Log.e("Baseeee8",URLHelper.BASE_URL1+"");
                Log.e("Baseeee",URLHelper.BASE_URL+"");
                System.setProperty("http.keepAlive", "false");

                retrofit = new Retrofit.Builder()
                        .baseUrl(URLHelper.BASE_URL1)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

           else if (apiCallTYpe.equalsIgnoreCase("5") )
            {
                Log.e("apiCallTYpe Inside",apiCallTYpe+"");
                System.setProperty("http.keepAlive", "false");

                retrofit = new Retrofit.Builder()
                        .baseUrl(URLHelper.BASE_URL_CHAT)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            else
            {
                Log.e("apiCallTYpe Inside1",apiCallTYpe+"");
                System.setProperty("http.keepAlive", "false");
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://13.235.235.100:9002")
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }


            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            if (mValue == 1)
            {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callGet(mUrl);
            }
            else if (mValue == 2)
            {
                Log.e("UrlPOst<<<: ",mUrl );
                mCall = retrofitApi.callPost(mUrl, RequestBody.create(MediaType.parse("application/json"),
                        mJsonObject.toString()));
            }
            else if (mValue == 3)
            {
                Log.e("UrlPosttttttt<<<: ",mUrl );
                mCall = retrofitApi.callPostttt(mUrl, map);
            }

            else if (mValue == 4)
            {
                Log.e("UrlPostttttttNew<<<: ",mUrl );
                mCall = retrofitApi.callPost1(mUrl,map1 );
            }
            else if (mValue == 5)
            {
                Log.e("Url5<<<: ",mUrl );
                mCall = retrofitApi.callMultipart(mUrl, map, mpart);
            }

            mCall.enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    String webRes = "";
                    try
                    {

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Log.e("SUcccessss<<<: ",mUrl );
                    Log.e("response<<<: ",response.toString());

                    if (response.isSuccessful())
                    {
                        try
                        {
                            String res = response.body().string();
                            Log.e("ResponseRetrofit  ",res+"");
                            mResponse.onResponse(mRequestCode, res);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.e("NotSuccesful  ",response.code()+"");
                        Log.e("NotSuccesfulBody  ",response.body()+"");
                    }

                   if (dialog.isShowing())
                   {
                        try
                        {
                            dialog.cancel();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                   }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    if (dialog.isShowing())
                    {
                        dialog.cancel();
                    }
                    t.printStackTrace();

                    Log.e( "onResponse:MerchantAppp ","Failue " );
                    alertOnTimeOut(mCall, this, "Connection Time out");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void alertOnTimeOut(final Call<ResponseBody> call, final Callback<ResponseBody> callback, String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mcontext);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog1, int which)
            {
//                dialog .show();

                call.clone().enqueue(callback);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
            @Override
            public void onClick(DialogInterface dialog1, int which)
            {
                dialog1.dismiss();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();





    }
}
