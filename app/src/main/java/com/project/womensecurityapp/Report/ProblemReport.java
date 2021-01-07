package com.project.womensecurityapp.Report;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProblemReport extends AppCompatActivity implements LocationListener {

    private static final String TAG = "ProblemReport";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;

    private RecyclerView recyclerView;
    private AdapterReport adapterReport;
    List<ModelReport> modelReportList;

    private ImageButton btnPhoto;
    private ImageButton btnAudio;

    private MediaPlayer mediaPlayer;

    private boolean audioPlayed = false;

    LocationManager locationManager;
    DatabaseReference databaseReferenceReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_report);

        mediaPlayer = MediaPlayer.create(this, R.raw.my_recording);

        Spinner spinner = findViewById(R.id.report_spinner);
        btnAudio = findViewById(R.id.report_audio_btn);
        btnPhoto = findViewById(R.id.report_photo_btn);

        btnAudio.setOnClickListener(view -> {
            playAudio();
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            releaseMediaPlayer();
        });

        recyclerView = findViewById(R.id.report_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        modelReportList = new ArrayList<>();

        databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                .child("1").child("Report");

        requestLocationPermission();

    }

    private void requestLocationPermission() {

        Log.d(TAG, "requestLocationPermission: requesting location permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                getDeviceLocation();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "Application required to location permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            getDeviceLocation();
        }

    }

    private void getDeviceLocation() {

        Log.d(TAG, "getDeviceLocation: fetching device location");
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: error");
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        ModelReport modelReport = new ModelReport();
        modelReport.setLatitude(String.valueOf(location.getLatitude()));
        modelReport.setLongitude(String.valueOf(location.getLongitude()));

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            modelReport.setPlace(address.get(0).getLocality());
        } catch (Exception e) {
            Log.d(TAG, "onLocationChanged: error");
        }

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String time = simpleDateFormat.format(calendar.getTime());
        modelReport.setTime(time);

        databaseReferenceReport.push().setValue(modelReport);

        makeTimelineReport();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    private void makeTimelineReport() {

        Log.d(TAG, "makeTimelineReport: called");

        databaseReferenceReport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                modelReportList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    ModelReport modelReport = ds.getValue(ModelReport.class);
                    modelReportList.add(modelReport);

                    adapterReport = new AdapterReport(getApplicationContext(), modelReportList);
                    recyclerView.setAdapter(adapterReport);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, "makeTimelineReport: Something went wrong");
            }
        });

    }

    private void playAudio() {
        if (audioPlayed) {
            //pause
            audioPlayed = false;
            mediaPlayer.pause();
            btnAudio.setImageResource(R.drawable.ic_play);
        } else {
            // play
            audioPlayed = true;
            mediaPlayer.start();
            btnAudio.setImageResource(R.drawable.ic_pause);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}
