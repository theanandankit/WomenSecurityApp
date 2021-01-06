package com.project.womensecurityapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.User_login_info.Account_setup;
import com.project.womensecurityapp.model.User_residential_details;
import com.project.womensecurityapp.model.location_model;
import com.project.womensecurityapp.model.person_details;
import com.project.womensecurityapp.model.person_info;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;


public class Main2Activity extends AppCompatActivity {

    private static final int STORAGE_REQUEST_CODE = 200;
    public static final int PERMISSION_REQUEST_CODE = 1234;

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    public static User_residential_details t = new User_residential_details();
    public static String status = "true";

    private String[] appPermissions;

    private String[] storagePermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getAppPermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestStoragePermission();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_history,
                R.id.nav_trusted, R.id.nav_contact, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerView.bringToFront();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                }
                if (id == R.id.nav_profile) {
                    Toast.makeText(getApplicationContext(), "Profile clicked", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_profile);
                }
                if (id == R.id.nav_history) {
                    Toast.makeText(getApplicationContext(), "History clicked", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_history);
                }
                if (id == R.id.nav_contact) {
                    navController.navigate(R.id.nav_contact);
                }
                if (id == R.id.nav_trusted) {
                    navController.navigate(R.id.nav_trusted);
                }
                if (id == R.id.nav_share) {
                    navController.navigate(R.id.nav_share);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;


            }
        });


        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    showDialog();
                } else {
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference.child("Personal_info").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                t = dataSnapshot.getValue(User_residential_details.class);
                            else {
                                status = "false";
                                Intent i = new Intent(Main2Activity.this, Account_setup.class);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(), "Not set", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout)
            auth.signOut();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(authStateListener);


        super.onStart();

    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Button login = dialog.findViewById(R.id.login);
        final Button Signup = dialog.findViewById(R.id.signup);
        final Button goto_ = dialog.findViewById(R.id.goto_id);

        login.setOnClickListener(v -> {
            Intent i = new Intent(Main2Activity.this, Login.class);
            startActivity(i);

        });

        Signup.setOnClickListener(v -> {
            Intent i = new Intent(Main2Activity.this, com.project.womensecurityapp.User_login_info.Signup.class);
            startActivity(i);

            if (!preferences.getString("active", "no").equals("no")) {
                goto_.setText("Goto recent action");
            }

        });

        goto_.setOnClickListener(v -> {

            if (preferences.getString("active", "no").equals("no")) {
                new_entry_track();
            } else {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);
            }
            dialog.dismiss();

        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    public void new_entry_track() {
        final Dialog dialog = new Dialog(Main2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_request_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editText = dialog.findViewById(R.id.new_entry_text);
        Button cancel = dialog.findViewById(R.id.new_entry_cancel);
        Button ok = dialog.findViewById(R.id.new_entry_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                showDialog();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.onEditorAction(EditorInfo.IME_ACTION_DONE);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                        .child(editText.getText().toString());

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                                    .child(editText.getText().toString()).child("status");

                            check.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot pdataSnapshot) {


                                    if (pdataSnapshot.getValue().toString().equals("active")) {
                                        Toast.makeText(getApplicationContext(), "Starting the live Tracking", Toast.LENGTH_LONG).show();
                                        editor.putString("problem-id", editText.getText().toString());
                                        editor.commit();

                                        new_user_info();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Problem is closed", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {

                            Toast.makeText(getApplicationContext(), "Wrong ID, Please Check it", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    public void new_user_info() {
        final Dialog dialog = new Dialog(Main2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText name = dialog.findViewById(R.id.new_entry_name);
        final EditText contact = dialog.findViewById(R.id.new_entry_contact);

        Button cancel = dialog.findViewById(R.id.new_entry_cancel2);
        Button ok = dialog.findViewById(R.id.new_entry_ok2);

        final int[] a = new int[1];

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("person").child("counter");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                a[0] = dataSnapshot.getValue().hashCode();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                person_details details = new person_details(new location_model("0", "0")
                        , new person_info(preferences.getString("current_user_name", "NA")
                        , preferences.getString("current_user_contact", "NA")), "1");

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference()
                        .child("Problem_Record")
                        .child(preferences.getString("problem-id", "1"))
                        .child("person")
                        .child("person_info")
                        .child("person_no_" + a[0]);

                databaseReference1.setValue(details);


                editor.putString("new_user_name", preferences.getString("current_user_name", "NA"));
                editor.putString("new_user_contact", preferences.getString("current_user_contact", "NA"));
                editor.putString("new_user_counter", String.valueOf(a[0]));
                editor.putString("active", "yes");
                editor.commit();

                a[0]++;

                databaseReference.setValue(a[0]);

                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(i);


                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void getAppPermissions() {

        appPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
                Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};

        ActivityCompat.requestPermissions(this, appPermissions, PERMISSION_REQUEST_CODE);

    }

}