package com.project.womensecurityapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;
import com.project.womensecurityapp.Location.BackgroundLocationService;
import com.project.womensecurityapp.adapter.trusted_person_adapter;
import com.project.womensecurityapp.model.Trusted_person_model;
import com.project.womensecurityapp.model.User_residential_details;
import com.project.womensecurityapp.model.location_model;

import java.util.ArrayList;

import static com.project.womensecurityapp.MainActivity.preferences;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int location_permission_request_code = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    Location currentLocation;
    DatabaseReference databaseReference_person_location;
    FloatingActionButton notice, trusted;

    // widgets
    ImageView gps_icon;

    // variables
    private Boolean location_permission_granted = false;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    GeoApiContext mGeoApiContext = null;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        trusted = findViewById(R.id.map_float_trusted);

        trusted.setOnClickListener(v -> trusted_perason());

        getLocationPermission();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("Location");
        databaseReference_person_location = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("person")
                .child("person_info").child("person_no_" + preferences.getString("new_user_counter", "1")).child("location");
    }

    private void init() {

        gps_icon = findViewById(R.id.map_gps_icon);

        gps_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                location_model location = new location_model();
//                location=dataSnapshot.getValue(location_model.class);

                location_model location = dataSnapshot.getValue(location_model.class);

                set_maker(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLocationPermission() {

        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), fine_location) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), coarse_location) == PackageManager.PERMISSION_GRANTED) {
                location_permission_granted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, location_permission_request_code);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, location_permission_request_code);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called");
        location_permission_granted = false;

        switch (requestCode) {
            case location_permission_request_code: {

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            location_permission_granted = false;
                            return;
                        }
                    }

                    Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    location_permission_granted = true;
                    //initialize map
                    initMap();

                }

            }
        }

    }

    private void initMap() {

        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        if (location_permission_granted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private void getDeviceLocation() {

        Log.d(TAG, "getDeviceLocation: getting the device current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (location_permission_granted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: location found");
                        currentLocation = (Location) task.getResult();
                        assert currentLocation != null;
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        location_model location1 = new location_model();
                        location1.setLongitude(String.valueOf(currentLocation.getLongitude()));
                        location1.setLatitude(String.valueOf(currentLocation.getLatitude()));

                        databaseReference_person_location.setValue(location1);
                        startBackgroundLocationService();

                    } else {
                        Log.d(TAG, "onComplete: Current Location is not found");
                        Toast.makeText(MapActivity.this, "Unable to find device's current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
    }

    public void set_maker(location_model maker_location) {

        try {


            LatLng latLng = new LatLng(Double.valueOf(maker_location.getLatitude()), Double.valueOf(maker_location.getLongitude()));

            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker
            markerOptions.position(latLng);

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);

            // Clears the previously touched position
            mMap.clear();

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            mMap.addMarker(markerOptions);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    girl_info_popup();
                    return false;
                }
            });

        } catch (NumberFormatException e) {
            finish();

            e.printStackTrace();
        }
    }

    private void startBackgroundLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, BackgroundLocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                MapActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.womensecurityapp.services.BackgroundLocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    public void girl_info_popup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_person_girl_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView name = dialog.findViewById(R.id.person_girl_name);
        final TextView address = dialog.findViewById(R.id.person_girl_address);
        final TextView contact = dialog.findViewById(R.id.person_girl_contact);
        Button cancel = dialog.findViewById(R.id.person_girl_cancel);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("problem-id").child(preferences.getString("problem-id", "1"));
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference databaseReference11 = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getValue().toString()).child("Personal_info");
                databaseReference11.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot pdataSnapshot) {
                        User_residential_details person = new User_residential_details();
                        person = pdataSnapshot.getValue(User_residential_details.class);

                        name.setText(person.getName());
                        address.setText(person.getHouse_no() + " " + person.getStreet() + " " + person.getCity() + " " + person.getCountry());
                        contact.setText(person.getContact_no());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    public void trusted_perason() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.trusted_person_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final ArrayList<Trusted_person_model> person = new ArrayList<>();

        final ListView listView = dialog.findViewById(R.id.trusted_person_popup_list);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("problem-id").child(preferences.getString("problem-id", "1"));
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getValue().toString()).child("Trusted_person").child("Info");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Trusted_person_model t = new Trusted_person_model();
                            t = postSnapshot.getValue(Trusted_person_model.class);
                            person.add(new Trusted_person_model(t.getName(), t.getContact(), "---", t.getAddress(), t.getRelation()));

                        }
                        trusted_person_adapter myAdapter = new trusted_person_adapter(getApplicationContext(), R.layout.trusted_person_model, person);
                        listView.setAdapter(myAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);

    }

}