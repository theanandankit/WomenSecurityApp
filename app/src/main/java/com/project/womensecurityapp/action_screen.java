package com.project.womensecurityapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.womensecurityapp.Location.BackgroundLocationService_Girls;
import com.project.womensecurityapp.Notification.notification_generator;
import com.project.womensecurityapp.Retrofit.ApiClient;
import com.project.womensecurityapp.model.RetrofitModel.PlaceResult;
import com.project.womensecurityapp.model.RetrofitModel.safeLocation;
import com.project.womensecurityapp.model.location_model;
import com.project.womensecurityapp.model.person_details;
import com.project.womensecurityapp.services.AppController;
import com.project.womensecurityapp.services.foreground_service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;

public class action_screen extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String NAME = "name";
    public static final String VICINITY = "vicinity";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;

    private String safe_location_FLAG;
    private String storagePath = "users_problem_photo_imgs/ ";
    private LocationManager locationManager;

    //widgets

    private Button alertButton;
    private LinearLayout mapButton;
    private TextView locationText;
    private ImageView gps_icon;
    private LinearLayout policeStationButton;
    private LinearLayout railwayStationButton;
    private LinearLayout airportButton;
    private LinearLayout mallButton;

    //Variables
    private Boolean location_permission_granted = false;
    DatabaseReference databaseReference_location;
    DatabaseReference databaseReference_person, databaseReference_person_info;
    private GoogleMap mMap;
    private Location myLocation;
    JSONArray jsonArray;
    FloatingActionButton camera, resend;
    private Uri image_uri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_screen);

        camera = findViewById(R.id.float_picture);
        resend = findViewById(R.id.float_resend);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notification_generator n = new notification_generator();
                n.send_notification(preferences.getString("current_user_name", "A lady") + " want your Help!", "problem ID (" + preferences.getString("problem-id", "NA") + ")", getApplicationContext());
            }
        });

        camera.setOnClickListener(v -> pickFromCamera());

        init();
        requestLocationPermission();

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // stop Background location service girl
                Intent intent = new Intent(action_screen.this, BackgroundLocationService_Girls.class);
                stopService(intent);

                //stop shake service
                Intent shakeIntent = new Intent(action_screen.this, foreground_service.class);
                stopService(shakeIntent);

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                        .child(preferences.getString("problem-id", "1")).child("status");
                databaseReference2.setValue("Inactive");

                editor.putString("girl-login", "no");
                editor.putString("active", "no");
                editor.putString("is_notification_send", "not_known");
                editor.putString("is_shake_happened", "not_known");
                editor.commit();

                finish();

            }
        });

        gps_icon.setOnClickListener(view -> getLocation());

        policeStationButton.setOnClickListener(view -> {
            String type = "police";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "police", Toast.LENGTH_LONG).show();
        });

        railwayStationButton.setOnClickListener(view -> {
            String type = "railway";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Railway Station", Toast.LENGTH_LONG).show();
        });

        airportButton.setOnClickListener(view -> {
            String type = "airport";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Airport", Toast.LENGTH_LONG).show();
        });

        mapButton.setOnClickListener(view -> {
            String type = "mall";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Shopping mall", Toast.LENGTH_LONG).show();
        });

    }

    private void init() {


        Log.e("counter", preferences.getString("problem-id", "147"));

        databaseReference_location = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                .child(preferences.getString("problem-id", "1")).child("Location");
        databaseReference_person = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                .child(preferences.getString("problem-id", "1")).child("person").child("person_info");


        // init widgets
        alertButton = findViewById(R.id.alertButton);
        locationText = findViewById(R.id.locationText);
        gps_icon = findViewById(R.id.action_screen_gps_icon);
        policeStationButton = findViewById(R.id.policeStationBtn);
        railwayStationButton = findViewById(R.id.railwayStationBtn);
        airportButton = findViewById(R.id.AirportBtn);
        mapButton = findViewById(R.id.MallBtn);

    }

    private void initMap() {

        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_actionScreen);
        mapFragment.getMapAsync(action_screen.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        // to add different person location into the map
        addAllPerson();

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!(marker.getTag() == null)) {
                    popup_window(marker.getTag().toString());
                }
                return false;
            }
        });


        if (location_permission_granted) {
            getLocation();
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
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }

    }

    private void addAllPerson() {

        databaseReference_person.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final MarkerOptions[] markerOptions = {null};
                    final Marker[] marker = new Marker[1];

                    databaseReference_person_info = databaseReference_person.child(postSnapshot.getKey());

                    databaseReference_person_info.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot pdataSnapshot) {

                            person_details person = new person_details();
                            person = pdataSnapshot.getValue(person_details.class);


                            if (markerOptions[0] == null) {

                                markerOptions[0] = new MarkerOptions();

                                LatLng latLng = new LatLng(Double.valueOf(person.getLocation().getLatitude()), Double.valueOf(person.getLocation().getLongitude()));

                                markerOptions[0].position(latLng);

                                markerOptions[0].title("NAME: " + person.getInfo().getName());


                                marker[0] = mMap.addMarker(markerOptions[0]
                                        .icon(bitmapDescriptorFromVector(getApplicationContext(),
                                                R.drawable.ic_person_pin_circle)));
                            } else {
                                LatLng latLng = new LatLng(Double.valueOf(person.getLocation().getLatitude()), Double.valueOf(person.getLocation().getLongitude()));
                                marker[0].setPosition(latLng);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                location_permission_granted = false;
                Toast.makeText(getApplicationContext(), "Application will not run without location permission",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                location_permission_granted = true;
                initMap();
            }
        }
    }

    private void requestLocationPermission() {

        Log.d(TAG, "requestLocationPermission: requesting location permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                location_permission_granted = true;
                initMap();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "Application required to location permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            initMap();
        }

    }

    void getLocation() {

        Log.d(TAG, "getLocation: fetching location");

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "getLocation: " + e.getMessage());
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;
        locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());

        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

        location_model location_data = new location_model();

        location_data.setLatitude(String.valueOf(location.getLatitude()));
        location_data.setLongitude(String.valueOf(location.getLongitude()));

        databaseReference_location.setValue(location_data);
        startBackgroundLocationService_Girls();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            locationText.setText(locationText.getText() + "\n" + addresses.get(0).getAddressLine(0) + "\n"
                    + addresses.get(0).getAddressLine(1) + "\n" + addresses.get(0).getAddressLine(2));

        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, "OnLocationChanged: error" + e.getMessage());

        }

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

    private void moveCamera(LatLng latLng, float zoom, String title) {

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
    }

    public void set_maker(location_model maker_location) {

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
        //    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
    }

    private void getNearByPlaces(String type) {

        ApiClient apiClient = new ApiClient();

        Call<PlaceResult> call = apiClient.getApiinterface().getData(type, "ZSskAK5jn2K7T6stJoJ1fyDXGDChSZuE", String.valueOf(myLocation.getLatitude()), String.valueOf(myLocation.getLongitude()));
        call.enqueue(new Callback<PlaceResult>() {
            @Override
            public void onResponse(Call<PlaceResult> call, Response<PlaceResult> response) {

                if (response.isSuccessful()) {
                    setMaker(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<PlaceResult> call, Throwable t) {

            }
        });
    }

    private void setMaker(ArrayList<safeLocation> result) {

        mMap.clear();
        addAllPerson();

        for (int a = 0; a < result.size(); a++) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(result.get(a).getPosition().getLat(), result.get(0).getPosition().getLon());
            markerOptions.position(latLng);
            markerOptions.title(result.get(a).getAddress().getStreetName() + " " + result.get(a).getAddress().getCountrySecondarySubDivision() + " " + result.get(a).getAddress().getMunicipality() + " " + result.get(a).getAddress().getCountrySecondarySubDivision() + " " + result.get(a).getAddress().getPostalCode());
            if (safe_location_FLAG == "police") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_security)));
                marker.setTag(String.valueOf(a));
            } else if (safe_location_FLAG == "railway") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_train)));
                marker.setTag(String.valueOf(a));
            } else if (safe_location_FLAG == "airport") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_airport)));
                marker.setTag(String.valueOf(a));
            } else if (safe_location_FLAG == "mall") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_store_mall)));
                marker.setTag(String.valueOf(a));
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void startBackgroundLocationService_Girls() {

        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, BackgroundLocationService_Girls.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                action_screen.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
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


    public void popup_window(String counter) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.police_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView name = dialog.findViewById(R.id.action_name);
        TextView address = dialog.findViewById(R.id.action_address);
        TextView rating = dialog.findViewById(R.id.action_rating);
        TextView person = dialog.findViewById(R.id.action_total_no_person);

        JSONObject place = null;
        try {
            place = jsonArray.getJSONObject(Integer.parseInt(counter));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!place.isNull(NAME)) {
                name.setText(place.getString(NAME));
            }

            if (!place.isNull(VICINITY)) {

                address.setText(place.getString(VICINITY));
            }

            if (!place.isNull("rating")) {
                Log.e("aa", String.valueOf(place.getDouble("rating")));
                rating.setText(place.getString("rating"));
            }


            if (!place.isNull("user_ratings_total")) {

                Log.e("as", String.valueOf(place.getDouble("user_ratings_total")));
                person.setText("(" + place.getString("user_ratings_total") + " person)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);


    }

    private void pickFromCamera() {

        try {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

            // put image uri
            image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // intent to start camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            cameraIntent.putExtra("android.intent.extra.quickCapture", true);
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
        } catch (Exception e) {
            Log.d(TAG, "pickFromCamera: " + e.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // this method will be called after picking image from camera
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadImageToFirebase();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToFirebase() {

//        String filePath = storagePath + "" + "" + "1";

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("1");
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG, "uploadProfileCoverPhoto: Successful");
                        Toast.makeText(action_screen.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(action_screen.this, "Something wrong happened " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "uploadImageToFirebase: OnFailure: " + e.getMessage());
                    }
                });

    }

    public void person_info_dilog(Double aDouble) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.person_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView name = dialog.findViewById(R.id.person_info_name);
        TextView contact = dialog.findViewById(R.id.person_info_contact);

    }

}