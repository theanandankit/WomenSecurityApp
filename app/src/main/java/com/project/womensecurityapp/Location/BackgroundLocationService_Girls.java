package com.project.womensecurityapp.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.womensecurityapp.Notification.notification_generator;
import com.project.womensecurityapp.Report.ModelReport;
import com.project.womensecurityapp.model.location_model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;

public class BackgroundLocationService_Girls extends Service {

    private static final String TAG = "BackgroundLocationService_Girls";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4000;
    private final static long FASTEST_INTERVAL = 2000;

    DatabaseReference databaseReference_location;
    DatabaseReference databaseReference_report;

    List<Address> address;
    Location currentLocation;
    int oldMinute, oldHour;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Girl Location Activity")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        databaseReference_report = FirebaseDatabase.getInstance().getReference()
                .child("Problem_Record")
                .child(preferences.getString("problem-id", "1"))
                .child("Report");

        oldHour = 0;
        oldMinute = 0;
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    @SuppressLint("LongLogTag")
    private void getLocation() {

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationG: stopping the location service.");
            stopSelf();
            return;
        }

        Log.d(TAG, "getLocationG: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResultG: got location result.");

                        currentLocation = locationResult.getLastLocation();

                        if (currentLocation != null) {

                            Log.d(TAG, "getLocationG: " + currentLocation.getLatitude() + "/" + currentLocation.getLongitude());

                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                address = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!preferences.getString("is_notification_send", "not_known").equals("yes")) {
                                Log.d(TAG, "preferences: notification");
                                try {
                                    Log.d(TAG, "preferences: called");
                                    notification_generator n = new notification_generator();
                                    n.send_notification(preferences.getString("current_user_name", "A lady") + " want your Help!", "Problem Id (" + preferences.getString("problem-id", "NA") + ")", getApplicationContext());

                                } catch (Exception e) {
                                    Log.d(TAG, "getLocation: catch Block");
                                }

                                editor.putString("is_notification_send", "yes");
                                editor.commit();
                            }

                            report();


                            // updating location in firebase
                            location_model location = new location_model();
                            location.setLongitude(String.valueOf(currentLocation.getLongitude()));
                            location.setLatitude(String.valueOf(currentLocation.getLatitude()));

                            databaseReference_location = FirebaseDatabase.getInstance().getReference()
                                    .child("Problem_Record")
                                    .child(preferences.getString("problem-id", "1"))
                                    .child("Location");

                            databaseReference_location.setValue(location);
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void report() {

        Log.d("Background Report", "report: called");

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh");
        String minute = simpleDateFormat.format(calendar.getTime());
        String hour = simpleDateFormat1.format(calendar.getTime());
        int currentMinute = Integer.parseInt(minute);
        int currentHour = Integer.parseInt(hour);


        if ((currentHour * 60 + currentMinute) - (oldHour * 60 + oldMinute) >= 1) {

            Log.d("Background Report", "report: updating report");

            oldHour = currentHour;
            oldMinute = currentMinute;

            ModelReport modelReport = new ModelReport();
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm:ss");
            String time = simpleDateFormat2.format(calendar.getTime());
            modelReport.setTime(time);
            modelReport.setLatitude(String.valueOf(currentLocation.getLatitude()));
            modelReport.setLongitude(String.valueOf(currentLocation.getLongitude()));
            try {
                modelReport.setPlace(address.get(0).getSubLocality());

                databaseReference_report.push().setValue(modelReport);
            } catch (Exception e) {

            }
        }
    }


}