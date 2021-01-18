package com.ibring_driver.provider;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.multidex.MultiDex;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Models.SignupData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;

public class XuberServicesApplication extends Application
{

    public static final String TAG = XuberServicesApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public SignupData signupData;
    private static Activity mActivity;


    private static XuberServicesApplication mInstance;


    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static void setActivity(Activity act)
    {
        mActivity=act;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //initCalligraphyConfig();
    }

    /*private void initCalligraphyConfig() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.bariol))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }*/


    private Socket mSocket;
    {
        try {
            Log.e("URL ",URLHelper.CHAT_SERVER_URL);
            mSocket = IO.socket(URLHelper.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket()
    {
        return mSocket;
    }

    public static synchronized XuberServicesApplication getInstance() {
        Log.w(TAG, "sendRequest: 3");
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        Log.w(TAG, "sendRequest: 6");
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json){
        String trimmedString = "";

        try{
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form",""+value.getString(i));
                        trimmedString += value.getString(i);

                        if(i < size-1) {
                            trimmedString += '\n';
                        }
                    }
                }
                catch (JSONException e)
                {
                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed",""+trimmedString);

        return trimmedString;
    }
}
