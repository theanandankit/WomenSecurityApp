package com.project.womensecurityapp.Shake;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.womensecurityapp.Location.BackgroundLocationService_Girls;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;

public class shake_service extends Service implements SensorEventListener {

    private static final String TAG = "shake_service";

    public final int MIN_TIME_BETWEEN_SHAKES = 3000;
    SensorManager sensorManager = null;

    Vibrator vibrator = null;

    private long lastShakeTime = 0;

    private Float shakeThreshold = 30.0f;

    public shake_service() {
    }

    @Override
    public void onCreate() {
        Log.e("kj", "uin");
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.e("kijj", "uin");

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                if (acceleration > shakeThreshold) {
                    lastShakeTime = curTime;

                    if (!preferences.getString("is_shake_happened", "not_known").equals("yes")) {

                        Log.e(TAG, preferences.getString("is_shake_happened", "not_known"));

                        Toast.makeText(getApplicationContext(), "problem is creating..", Toast.LENGTH_LONG).show();

                        if (!isLocationServiceRunning()) {

                            Intent serviceIntent = new Intent(this, BackgroundLocationService_Girls.class);

                            final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("problem-id").child("counter");
                            int a = Integer.parseInt(preferences.getString("problem-id", "1"));

                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("status");
                            databaseReference2.setValue("active");

                            DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference().child("problem-id").child(preferences.getString("problem-id", "1"));
                            databaseReference4.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("person").child("counter");
                            databaseReference.setValue("0");

                            a++;


                            databaseReference1.setValue(String.valueOf(a));

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                getApplicationContext().startForegroundService(serviceIntent);
                            } else {
                                startService(serviceIntent);
                            }


                            editor.putString("girl-login", "yes");
                            editor.putString("is_shake_happened", "yes");
                            editor.commit();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "problem already created", Toast.LENGTH_LONG).show();
                        Log.e(TAG, preferences.getString("is_shake_happened", "not_known"));
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isLocationServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.womensecurityapp.services.BackgroundLocationService_Girls".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

}
