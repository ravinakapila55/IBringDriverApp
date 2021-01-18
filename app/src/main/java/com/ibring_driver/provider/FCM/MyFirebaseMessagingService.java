package com.ibring_driver.provider.FCM;

/**
 * Created by jayakumar on 16/02/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ibring_driver.provider.Activity.Home;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.Models.FoodRequestModel;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.Utilities;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


//new incoming request
/* {data={"restaurant_id":5,"latitude":"75.7551023","discount":null,"created_at":"2020-05-12 05:54:22",
"restaurant_commission":null,"updated_at":"2020-05-13 12:12:47","dine_in_commission":null,"payment_id":null,
"price":"2700","service_id":18,"id":119,"longitude":"31.7413754","payment_mode":"cash",
"item":"[{\"menu_id\":\"18\",\"quantity\":\"7\",\"item_name\":\"Deserts\",\"price\":\"300\"},{\"menu_id\":\"19\",\"quantity\":\"3\",\"item_name\":\"Shakes\",\"price\":\"200\"}]","hours":"0",
"minutes":"12","ibring_commission":null,"tax":null,"registration_fee":null,"delivery_charge":null,
"user_id":142,"location":"Bus Stop, Dasuya - Hoshiarpur Rd, Garhdiwala, Punjab 144207, India",
"order_id":"5EBA3A0E1C370","delivery_boy_id":199,"user":{"stripe_cust_id":null,"payment_mode":"CASH",
"device_id":"730d855e8af3eca2","latitude":null,"mobile":"4758692310","rating":"5.00","last_name":"testuser",
"wallet_balance":0,"device_type":"android","otp":0,"email_verified_at":"2018-12-22 18:12:34","picture":"","rating_count":0,
"social_unique_id":"","device_token":"e4ThgtgY82g:APA91bGqNkI2MvKroA8aHq6VLDEFcAbU7yYO2FEdbOpLdVP3KN1UMZ37X8GAO4og5mNv-n3sua3QttJxbMvZroQdAaO-W2FI-GrBpSQ00avzNM0IDE99kTnV-2QuLVJ7vfbFsyHWO3RV",
"id":142,"first_name":"testuser","login_by":"manual",
"email":"ut@mailinator.com","longitude":null},"status":"accept"},
type=Food delivery, message=Food delivery}*/

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseMsgService";
    Utilities utils = new Utilities();
    private Map<String, String> data;
    private String  type;
    private String title, message;


    /* {data={"restaurant_id":5,"latitude":"75.7544102","discount":null,
    "created_at":"2020-07-31 05:51:56","restaurant_commission":null,"updated_at":"2020-07-31 05:55:28",
    "dine_in_commission":null,"payment_id":null,"price":"900","service_id":18,"id":224,
    "longitude":"31.7461055","payment_mode":"cash","item":[{"quantity":"3","price":"300","item_name":"Pizza","menu_id":"21"}],
    "hours":"0","minutes":"12","ibring_commission":null,"restaurant":{"image":"restaurant\/830d978f8d018caddb7108d7c428b8dc.png",
    "hours":"0","address":"Garhdiwala, Punjab, India","lattitude":"31.7412342","minutes":"12","rating":"0","description":"kopko",
    "title":"kapila90","deleted_at":null,"provider_id":171,"id":5,"status":"1","longitude":"75.7560455"},"tax":null,
    "registration_fee":null,"delivery_charge":null,"user_id":315,
    "location":"Hoshiarpur - Dasuya Rd, Garhdiwala, Punjab 144207, India","order_id":"5F23B17C33D15",
    "delivery_boy_id":222,"user":{"stripe_cust_id":null,"payment_mode":"CASH","device_id":"730d855e8af3eca2",
    "latitude":null,"mobile":"494948161616","rating":"5.00","last_name":"new","wallet_balance":0,
    "device_type":"android","otp":0,"email_verified_at":"2020-07-31 05:01:16","picture":"","rating_count":0,
    "social_unique_id":"","device_token":"c0a5EdVvKbQ:APA91bHbscQKYcPTJ_4WDEDZDpG-m62eDo-JZ3XDKY2kUW4Qj18WYvCLYB9dh
    zRwc1YP76LhHkO2EyeD9PtR8hCtBJ7ceyEw4AMFMnmex7mw08Ai5RAn0Nc2nmt1eoReGjcmwWjzFA6f","id":315,"first_name":"new",
    "login_by":"manual","email":"new@mailinator.com","longitude":null},"status":"pending"},
     type=Food delivery, message=Food delivery}*/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        if(remoteMessage.getData()!=null)
        {
            data = remoteMessage.getData();
            Log.e(TAG, "onMessageReceived: "+remoteMessage.getData());
            message = data.get("message");
            type = data.get("type");
            Log.e(TAG, "message "+message);
            Log.e(TAG, "type "+type);

            if (!SharedHelper.getKey(getApplicationContext(), "id").equals(""))
            {
                Log.e(TAG, "InsideUserIdCondition "+"inside");
                if (URLHelper.checkActivation(getApplicationContext()))
                {
                    Log.e(TAG, "InsideCheckActivation "+"inside");
                    try
                    {
                        String obj = data.get("data");
                        Log.e(TAG, "obj "+obj);

                        if (type.equalsIgnoreCase("Food delivery"))
                        {
                            Log.e(TAG, "insideType "+type);

                            ArrayList<FoodRequestModel> foodList=new ArrayList<>();
                            foodList.clear();

                            JSONObject jsonObject = new JSONObject(obj);
                            Log.e(TAG, "InsidejsonObject "+jsonObject.toString());

                            JSONObject user = jsonObject.getJSONObject("user");
                            JSONObject restaurant = jsonObject.getJSONObject("restaurant");
                            Log.e(TAG, "InsideUserObject "+user.toString());

                            Intent intent = new Intent(this, Home.class);
                            FoodRequestModel foodRequestModel=new FoodRequestModel();
                            Bundle bundle = new Bundle();
                            foodRequestModel.setOrder_id(jsonObject.getString("id"));
                            foodRequestModel.setPrice(jsonObject.getString("price"));
                            foodRequestModel.setUser_loc(jsonObject.getString("location"));
                            foodRequestModel.setLattitude(jsonObject.getString("latitude"));
                            foodRequestModel.setLongitude(jsonObject.getString("longitude"));
                            foodRequestModel.setRest_id(jsonObject.getString("restaurant_id"));
                            foodRequestModel.setOrder_item(jsonObject.getString("item"));
                            foodRequestModel.setUser_name(user.getString("first_name"));
                            foodRequestModel.setRest_name(restaurant.getString("title"));
                            foodRequestModel.setRest_loc(restaurant.getString("address"));
                            foodRequestModel.setRest_lat(restaurant.getString("lattitude"));
                            foodRequestModel.setRest_lng(restaurant.getString("longitude"));
                            foodList.add(foodRequestModel);

                            intent.putExtra("key","notiFood");
                            intent.putExtra("data",(Serializable)foodList);
                            bundle.putString("key","notiFood");

                            intent.putExtras(bundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(121);
                            startActivity(intent);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            sendNotification(message);
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    Log.e(TAG, "ElseCHeckActivations "+"inside");
                }
            }
            else
            {
                Log.e(TAG, "ElseUserId "+"inside");
            }

        }
    }

    private void sendNotification(String messageBody)
    {
        Log.e(TAG, "sendNotification "+messageBody.toString());
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
