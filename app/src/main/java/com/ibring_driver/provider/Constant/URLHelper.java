package com.ibring_driver.provider.Constant;

import android.app.ActivityManager;
import android.content.Context;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class URLHelper
{
//http://178.128.116.149/ibring/public/api/provider/register
    public static final String BASE_URL="http://178.128.116.149/ibring/public";
    public static final String BASE_URL1="http://178.128.116.149/ibring/public/";
    public static final String BASE_URL_CHAT="http://178.128.116.149:7003/";
    public static final int    CLIENT_ID = 5;

    public static final String  CLIENT_SECRET_KEY = "gehZMcyHIkaSuxQado1EzmGyDSAi3NqzHwEqTp9N";
    public static final String  APP_LINK = "https://play.google.com/store/apps/details?id=com.taxicabs_sp.provider";

                                // Help
    public static final String HELP_REDIRECT_URL = BASE_URL + "";
//  public final static String PLACES_API="AIzaSyAK5It4p1CiJ2gFzWRbfs24Cibo2QTcPRU";
    public final static String PLACES_API="AIzaSyAAgL20f3ydrQcvvl77vldDinOqPNjv0LY";

    // Image load options URL
//  public static final String BASE_IMAGE_LOAD_URL_WITH_STORAGE = BASE_URL +"/storage/";
    public static final String BASE_IMAGE_LOAD_URL_WITH_STORAGE ="http://178.128.116.149/ibring/storage/app/public/";


    //    //    // http://178.128.116.149/ibring/storage/app/public/provider//profile//e7cc26b093b3941f3fcb651062d8e38a.jpeg


//    public static final String CHAT_SERVER_URL ="http://178.128.116.149:7003";
//  public static final String CHAT_SERVER_URL = "http://192.168.1.49:7003" ;
public static final String CHAT_SERVER_URL ="http://13.235.235.100:8090";


    //  public final static String SOCKET_URL = "http://165.22.215.99:9002/";
//    public final static String SOCKET_URL_LIVE_TRACK="http://192.168.1.49:9002/";
//    public final static String SOCKET_URL_LIVE_TRACK="http://68.183.91.242:9002/";
    public final static String SOCKET_URL_LIVE_TRACK="http://13.235.235.100:9002/";

                               // WEB API LIST
    public static final String LOGIN = BASE_URL + "/api/provider/oauth/token";
    public static final String REGISTER = BASE_URL + "/api/provider/register";
    public static final String PROVIDER_PROFILE = BASE_URL + "/api/provider/profile";
    public static final String PROVIDER_PROFILE_UPDATE = BASE_URL + "/api/provider/profile";
    public static final String CANCEL_REQUEST_API = BASE_URL + "/api/provider/cancel";
    public static final String GET_HISTORY_API = BASE_URL + "/api/provider/trips";

    public static final String GET_HISTORY_DETAILS_API = BASE_URL + "/api/provider/requests/history/details";
    public static final String CHANGE_PASSWORD_API = BASE_URL + "/api/provider/profile/password";
    public static final String UPCOMING_TRIP_DETAILS = BASE_URL + "/api/provider/requests/upcoming/details";
    public static final String UPCOMING_TRIPS = BASE_URL + "/api/provider/requests/upcoming";
    public static final String GET_HISTORY_REQUEST = BASE_URL + "/api/provider/requests/history";
    public static final String UPDATE_SERVICE = BASE_URL + "/api/provider/update/service";
    public static final String GET_SERVICES = BASE_URL + "/api/provider/services";

    public static final String UPDATE_AVAILABILITY_API = BASE_URL + "/api/provider/profile/available";
    public static final String RESET_PASSWORD = BASE_URL + "/api/provider/reset/password";
    public static final String FORGET_PASSWORD = BASE_URL + "/api/provider/forgot/password";
    public static final String SHOW_PROFILE = BASE_URL + "/api/provider/user";
    public static final String LOGOUT = BASE_URL + "/api/provider/logout";
    public static final String SUMMARY = BASE_URL + "/api/provider/summary";
    public static final String GET_HELP_DETAILS = BASE_URL + "/api/provider/help";
    public static final String GET_TARGET_FOR_SUMMARY = BASE_URL + "/api/provider/target";
    public static final String GET_APP_SERVICES = "api/provider/service-list";

                    /* ------ New Apis I Bring ------ */
    public static final String GET_CAB_TYPE = "api/provider/cab-list";
    public static final String GET_BRAND_LIST = "api/user/get-brand";
    public static final String GET_MODEL_LIST = "api/user/get-model";

    public static final String GET_CAPACITY_LIST = "api/user/get-cc";
    public static final String GET_CONERSATION_ID = "api/chat/conversation/id";
    public static final String GET_CONVERSATION = "api/chat/message";
    public static final String DELETE_CONVERSATION = "api/chat";

    public static final String ACTION_BY_DELIVERY_BOY_ON_FOOD_REQUEST = "api/provider/delivery-boy-request";
    public static final String ACTIVE_ORDERS_FOR_DELIVERY_BOY = "api/provider/progress-delivery";
    public static final String STATUS_CHANGE_API_BY_DELIVERY_BOY = "api/provider/status-order";
    public static final String FOOD_HISTORY = "api/provider/progress-delivery";
    public static final String COURIER_HISTORY = "api/provider/progress-delivery";
    public static final String ACTION_BY_DELIVERY_BOY_ON_COURIER_REQUEST = "api/provider/courier-boy-request";
    public static final String STATUS_CHANGE_BY_DRIVER_FOR_COURIER = "api/provider/courier-status";



    /*http://178.128.116.149/ibring/public/api/provider/courier-status
params :order_id,status
*/
//'pending','accept','delivered','cancel','out_delivery','on_going','pickup'


    /**/

    //http://178.128.116.149/ibring/public/api/provider/status-order

    /*https://vipankumar.in/ibring/public/api/provider/courier-boy-request
param   order_id,  status: cancel,accept



*/

    public static final String CHAT_TIME_FORMAT ="dd-mm-yyyy HH:mm a";

    public static String getFormatedTime(String dateStr, String strReadFormat, String strWriteFormat)
    {
        try
        {
            String formattedDate = dateStr;

            DateFormat readFormat = new SimpleDateFormat(strReadFormat);
            readFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            DateFormat writeFormat = new SimpleDateFormat(strWriteFormat);
            writeFormat.setTimeZone(TimeZone.getDefault());
            Date date = null;

            try
            {
                date = readFormat.parse(dateStr);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if (date != null) {
                formattedDate = writeFormat.format(date);
            }

            return formattedDate;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return dateStr;
        }
    }

    public static boolean checkActivation(Context context)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString()))
        {
            isActivityFound = true;
        }
        return isActivityFound;
    }

    public static String getFormattedDate(String dateStr, String strReadFormat, String strWriteFormat)
    {
        String formattedDate = dateStr;
        DateFormat readFormat = new SimpleDateFormat(strReadFormat);
        DateFormat writeFormat = new SimpleDateFormat(strWriteFormat);
        Date date = null;

        try
        {
            date = readFormat.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        if (date != null)
        {
            formattedDate = writeFormat.format(date);
        }

        return formattedDate;
    }
}
