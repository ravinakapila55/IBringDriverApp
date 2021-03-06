package com.ibring_driver.provider.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.ibring_driver.provider.Activity.ActivityPassword;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.ConnectionHelper;
import com.ibring_driver.provider.Helper.CustomDialog;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.CircularProgressBar;
import com.ibring_driver.provider.XuberServicesApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static com.ibring_driver.provider.XuberServicesApplication.trimMessage;

public class EarningsFragment extends Fragment {

    public static final String TAG = "EarningsFragment";

    Boolean isInternet;
    Activity activity;
    Context context;
    View rootView;
    EarningsAdapter earningsAdapter;
    RecyclerView rcvRides;
    RelativeLayout errorLayout;
    ConnectionHelper helper;
    CustomDialog customDialog;
    TextView lblTarget, lblEarnings;
    ImageView imgBack;
    CircularProgressBar custom_progressBar;
    AlertDialog alert;

    public EarningsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_earnings, container, false);
        findViewByIdAndInitialize();

        if (isInternet) {
            getEarningsList();
        } else {
            showDialog();
        }

        return rootView;
    }


    public void findViewByIdAndInitialize() {
        rcvRides = (RecyclerView) rootView.findViewById(R.id.rcvRides);
        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);
        lblTarget = (TextView) rootView.findViewById(R.id.lblTarget);
        lblEarnings = (TextView) rootView.findViewById(R.id.lblEarnings);
        imgBack = (ImageView) rootView.findViewById(R.id.backArrow);
        custom_progressBar = (CircularProgressBar) rootView.findViewById(R.id.custom_progressBar);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getEarningsList() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.GET_TARGET_FOR_SUMMARY, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("EarningResponse ",response+"");
                try {
                    if (response != null) {
//                        lblTarget.setText(response.optString("rides_count")+"/"+response.optString("target"));

                        custom_progressBar.setProgress(Integer.parseInt(response.optString("rides_count")));

                        float total_price = 0;

                        float sum=0;

                        for (int i = 0; i < response.optJSONArray("rides").length(); i++) {
                            try {
                                JSONObject jsonObject = response.optJSONArray("rides").getJSONObject(i);
                                JSONObject jsonObject2 = jsonObject.getJSONObject("payment");
                                total_price = jsonObject2.optInt("total");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            sum=sum+total_price;
                            Log.e("SUm ",sum+"");
                        }




                        animateTextView(0, Integer.parseInt(response.optString("rides_count")),
                                Integer.parseInt(response.optString("target")), lblTarget);

//                        custom_progressBar.setProgress(Integer.parseInt(response.optString("rides_count")));
                        int animationDuration = 1500; // 1500ms = 1,5s
                        int progress = Integer.parseInt(response.optString("rides_count")); // Progress
                        custom_progressBar.setProgressWithAnimation(progress, animationDuration);

//                        String strTotalPrice = Math.round(total_price) + "";
                        String strTotalPrice = Math.round(sum) + "";
                        String currency = SharedHelper.getKey(context, "currency");





//                        lblEarnings.setText(currency + strTotalPrice);
                        lblEarnings.setText(currency + strTotalPrice);

                        earningsAdapter = new EarningsAdapter(response);
                        //  recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                        rcvRides.setLayoutManager(mLayoutManager);
                        rcvRides.setItemAnimator(new DefaultItemAnimator());
                        if (earningsAdapter != null && earningsAdapter.getItemCount() > 0) {
                            rcvRides.setVisibility(View.VISIBLE);
                            errorLayout.setVisibility(View.GONE);
                            rcvRides.setAdapter(earningsAdapter);
                        } else
                       {
                            lblEarnings.setText(SharedHelper.getKey(context, "currency")+""+"0");
                            errorLayout.setVisibility(View.VISIBLE);
                            rcvRides.setVisibility(View.GONE);
                        }

                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                        rcvRides.setVisibility(View.GONE);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                customDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
                customDialog.dismiss();

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

    public void GoToBeginActivity()
    {
        Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(context, "current_status", "");
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public void displayMessage(String toastString)
    {
        Snackbar snackbar = Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
       /* TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));*/
        snackbar.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    private class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.MyViewHolder> {
        JSONObject jsonResponse;

        public EarningsAdapter(JSONObject jsonResponse) {
            this.jsonResponse = jsonResponse;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.earnings_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            JSONArray jsonArray = jsonResponse.optJSONArray("rides");
            try {

                JSONObject jsonObject = jsonArray.getJSONObject(position);
                try {
                    holder.lblTime.setText(getTime(jsonObject.optString("assigned_at")));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.lblDistance.setText(jsonObject.optJSONObject("service_type").optString("name"));
                holder.lblAmount.setText(SharedHelper.getKey(context, "currency") +
                        jsonArray.getJSONObject(position).getJSONObject("payment").optString("total"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount()
        {
            return jsonResponse.optJSONArray("rides").length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView lblTime, lblDistance, lblAmount;

            public MyViewHolder(View itemView)
            {
                super(itemView);
                lblTime = (TextView) itemView.findViewById(R.id.lblTime);
                lblDistance = (TextView) itemView.findViewById(R.id.lblDistance);
                lblAmount = (TextView) itemView.findViewById(R.id.lblAmount);
            }
        }
    }

    private String getTime(String date) throws ParseException
    {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }

    public void animateTextView(int initialValue, int finalValue, final int target, final TextView textview)
    {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(0.8f);
        int start = Math.min(initialValue, finalValue);
        int end = Math.max(initialValue, finalValue);
        int difference = Math.abs(finalValue - initialValue);
        Handler handler = new Handler();
        for (int count = start; count <= end; count++)
        {
            int time = Math.round(decelerateInterpolator.getInterpolation((((float) count) / difference)) * 100) * count;
            final int finalCount = ((initialValue > finalValue) ? initialValue - count : count);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textview.setText(finalCount + "/" + target);
                }
            }, time);
        }
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                        alert.dismiss();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }
}
