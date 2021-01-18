package com.ibring_driver.provider.chat;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    TextView txt_recieve;
    TextView txt_time;


    public ReceivedMessageViewHolder(View view) {
        super(view);

        txt_recieve=(TextView)view.findViewById(R.id.txt_recieve);
        txt_time=(TextView)view.findViewById(R.id.txt_time);


    }

    public void bind(ChatModel model, int position)
    {

        txt_recieve.setText(model.getMessage());
        SimpleDateFormat input=new SimpleDateFormat("dd-mm-yyyy hh:mm a");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm a");

        String time="";
        Date date=null;

        try {
            date=input.parse(model.getMessage_time());
            time=output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txt_time.setText(time);

    }

    public static String getFormatedDateTime(String dateStr, String strReadFormat, String strWriteFormat) {


        try {
            String formattedDate = dateStr;

            DateFormat readFormat = new SimpleDateFormat(strReadFormat, Locale.getDefault());
            DateFormat writeFormat = new SimpleDateFormat(strWriteFormat, Locale.getDefault());

            Date date = null;

            try {
                date = readFormat.parse(dateStr);
            } catch (ParseException e) {
            }

            if (date != null) {
                formattedDate = writeFormat.format(date);
            }

            return formattedDate;
        }
        catch (Exception e){

            return dateStr;

        }
    }


}

