package com.ibring_driver.provider.liveTrack;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.Utils.CustomMarker;


import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class LiveTrackBooking extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {


    private static final String TAG = LiveTrackBooking.class.getSimpleName();

    TextView toolbar_title;
    ImageView ivBack;

    String request_id="";
    String pickLocation="";
    String dropLocation="";

    LatLng pickLatlng=null;
    LatLng dropLatlng=null;

    private double pick_lat = 0;
    private double pick_lng = 0;
    private double drop_lat = 0;
    private double drop_lng = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_track);
        init();
    }

    Socket mSocket;

    public void connectSocket(String rideId)
    {
        try
        {
            Log.e(TAG, "connectSocket: " + "inside");
            Log.e(TAG, "Url: " + URLHelper.SOCKET_URL_LIVE_TRACK + rideId);

            mSocket = IO.socket(URLHelper.SOCKET_URL_LIVE_TRACK + rideId);

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);

            mSocket.on("callbackLoc", callbackLoc);
//            mSocket.on("callbackStatus", callbackStatus);
            mSocket.connect();
            //emitRoomJoin();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    static Marker marker;

    private Emitter.Listener callbackLoc = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "onConnectResponseOfSocketUpdate " + args[jj]);
                        try
                        {
                            JSONObject data = (JSONObject) args[0];
                            String latt = data.getString("lat");
                            String longg = data.getString("lng");

                            Log.e("Lattitudeee ",latt);
                            Log.e("Longitudeee ",longg);

                            LatLng latLngDriverUpdate = new LatLng(Double.parseDouble(latt),
                                    Double.parseDouble(longg));
                            Log.e(TAG, "run:LatLngDriver "+latLngDriverUpdate+"");
                            Log.e(TAG, "marker "+marker+"");

                            if (marker != null)
                            {
                                Log.e(TAG, "markerIF "+marker+"");
                                animateCar(latLngDriverUpdate);
                                boolean contains = mMap.getProjection()
                                        .getVisibleRegion()
                                        .latLngBounds
                                        .contains(latLngDriverUpdate);
                                if (!contains)
                                {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDriverUpdate));
                                }
                            }

                            else
                            {
                                Log.e(TAG, "markerElse "+marker+"");
                                Log.e("else ","else"+"");
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDriverUpdate, 17f));


                                marker = mMap.addMarker(new MarkerOptions().position(latLngDriverUpdate).
                                        icon(BitmapDescriptorFactory.fromBitmap(CustomMarker.
                                                getMarkerViewMovement(LiveTrackBooking.this))));

                            }



                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };


    private interface LatLngInterpolator
    {
        LatLng interpolate(float fraction, LatLng a, LatLng b);
        class LinearFixed implements LatLngInterpolator
        {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b)
            {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180)
                {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
    private void animateCar(final LatLng destination)
    {
        Log.e("animateCar ",destination+"");
        final LatLng startPosition = marker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

        Log.e("startPosition ",startPosition+"");
        Log.e("endPosition ",endPosition+"");

         /*  Toast.makeText(LiveTrackBooking.this, "StartPosition "
                   +startPosition+" end position "+endPosition, Toast.LENGTH_SHORT).show(); */

        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                try
                {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);

                    marker.setPosition(newPosition);
//                       marker.setAnchor(0.5f, 0.5f);
                    double bearing = bearingBetweenLocations(startPosition, endPosition);
                    changeMarkerPosition(bearing);


                    // rotateMarker(marker, (float) bearing);
//                       marker.setRotation(getBearing(startPosition, newPosition));

                     /*  map.animateCamera(CameraUpdateFactory
                   .newCameraPosition(new CameraPosition.Builder().target(newPosition).zoom(15.5f).build()));
                   */


                    /*


                     map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                               .target(newPosition)
                               .zoom(15.5f)
                               .build()));

                     */
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
            }
        });

        valueAnimator.start();

    }



    private static float angle;

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2)
    {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }

    private void changeMarkerPosition(double position)
    {
        float direction = (float) position;
//        Log.e("LocationBearing", "" + direction);

        if (direction==360.0){
            //default
            marker.setRotation(angle);
        }  else
        {
            marker.setRotation(direction);
            angle=direction;
        }
    }


    private Emitter.Listener onConnect = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            Log.e(TAG, "call: onConnect" + args.length);
            Log.e(TAG, "call: onConnect" + args.toString());


        }
    };


    private Emitter.Listener onDisconnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onDisconnect" + args.length);
//            Toast.makeText(OnTripDriver.this, "DisconnectSocket ", Toast.LENGTH_SHORT).show();


        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onConnectError" + args.length);
        }
    };

    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            Log.e(TAG, "call: onTimeConnectError" + args.length);
        }
    };
    GoogleMap mMap;
    SupportMapFragment mapFragment;

    void initMap()
    {
       /* if (mMap == null)
        {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }*/
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.provider_map);
        Log.e(TAG, "initializeMap: fragmentManager "+fm );
        fm.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        try
        {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.style_json));
            if (!success)
            {
                Log.e("Map:Style", "Style parsing failed.");
            }
            else
            {
                Log.e("Map:Style", "Style Applied.");
            }
        }
        catch (Resources.NotFoundException e)
        {
            Log.e("Map:Style", "Can't find style. Error:");
        }


        mMap = googleMap;

        setupMap();

        setTrack();



    }

    public void setTrack()
    {
        pickLatlng=new LatLng(pick_lat,pick_lng);
        dropLatlng=new LatLng(drop_lat,drop_lng);

        /*if (pickLatlng!=null && dropLatlng!=null)
        {
            new TrackGoogleLocation(LiveTrackBooking.this, mMap, LiveTrackBooking.this).
                    setMarkerLocate(pickLatlng, dropLatlng, 10,
                            pickLocation, dropLocation);
        }
*/

    }

    // set up settings for map
    @SuppressWarnings("MissingPermission")
    private void setupMap()
    {
        if (mMap != null)
        {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }
    }

    public static final int MY_PERMISSIONS_ACCESS_FINE = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void init()
    {
        toolbar_title=(TextView)findViewById(R.id.toolbar_title);
        ivBack=(ImageView) findViewById(R.id.ivBack);
        toolbar_title.setText("Live Track");

        ivBack.setOnClickListener(this);

        ivBack.setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("request_id"))
        {
            request_id=getIntent().getExtras().getString("request_id");
            Log.e("RequestId ",request_id);

            pick_lat= Double.parseDouble(getIntent().getExtras().getString("pick_lat"));
            pick_lng= Double.parseDouble(getIntent().getExtras().getString("pick_lng"));
            drop_lat= Double.parseDouble(getIntent().getExtras().getString("drop_lat"));
            drop_lng= Double.parseDouble(getIntent().getExtras().getString("drop_lng"));
            pickLocation=(getIntent().getExtras().getString("pic_loc"));
            dropLocation= (getIntent().getExtras().getString("drop_loc"));
        }

        connectSocket(request_id);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE);
        }
        else
        {
            initMap();
            MapsInitializer.initialize(LiveTrackBooking.this);
        }


     //   connectSocket(request_id);
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.ivBack:
                finish();
                break;
        }

    }


    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraIdle() {

    }


}
