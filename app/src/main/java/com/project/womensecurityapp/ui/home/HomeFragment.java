package com.project.womensecurityapp.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.MapActivity;
import com.project.womensecurityapp.R;
import com.project.womensecurityapp.SMS.SMS;
import com.project.womensecurityapp.User_login_info.Account_setup;
import com.project.womensecurityapp.action_screen;
import com.project.womensecurityapp.model.Trusted_person_model;
import com.project.womensecurityapp.model.User_residential_details;
import com.project.womensecurityapp.model.location_model;
import com.project.womensecurityapp.model.person_details;
import com.project.womensecurityapp.model.person_info;
import com.project.womensecurityapp.services.foreground_service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    public static final String tag_service = "MyServiceTag";
    public static final int REQUEST_PERMISSION_CODE = 1000;
    private static final int SEND_SMS_PERMISSION_REQUEST = 0;

    private DatabaseReference databaseReference_sms;
    private HomeViewModel homeViewModel;
    private Button shareLocation, help, start, recent;

    //variables
    String pathSave = "";
    MediaRecorder mediaRecorder;
    String latitude = "26.2492389";
    String longitude = "78.1727378";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView name = root.findViewById(R.id.home_name);
        final TextView contact = root.findViewById(R.id.home_contact);

        ToggleButton shakeButton = root.findViewById(R.id.shakeToggleButton);
        shakeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    startShakeService();
                } else {
                    stopShakeService();
                }

            }
        });

        ToggleButton recodingButton = root.findViewById(R.id.RecordingToggleButton);
        recodingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    if (checkPermissionFromDevice()) {
                        startRecording();
                    } else {
                        requestAudioPermission();
                    }
                } else {
                    stopRecording();
                }
            }
        });

        final DatabaseReference databaseReference_coubter = FirebaseDatabase.getInstance().getReference().child("problem-id").child("counter");
        databaseReference_coubter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String a = dataSnapshot.getValue().toString();

                editor.putString("problem-id", a);
                editor.commit();
                Log.e("counter1", preferences.getString("problem-id", "1478"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Personal_info");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        User_residential_details u = new User_residential_details();
                        u = dataSnapshot.getValue(User_residential_details.class);

                        name.setText(u.getName());
                        contact.setText(u.getContact_no());
                        editor.putString("current_user_name", u.getName());
                        editor.putString("current_user_contact", u.getContact_no());
                        editor.putString("current_user_city", u.getCity().toUpperCase());
                        editor.commit();
                    } else {
                        Intent i = new Intent(getActivity(), Account_setup.class);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }


        shareLocation = root.findViewById(R.id.type1);
        help = root.findViewById(R.id.help);

        start = root.findViewById(R.id.home_enter);
        recent = root.findViewById(R.id.home_recent_activity);

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("active", "no").equals("no")) {
                    Toast.makeText(getActivity(), preferences.getString("active", "no"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getActivity(), MapActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "You don't have recent activity", Toast.LENGTH_LONG).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("active", "no").equals("no")) {
                    new_entry_track();
                } else {
                    recent_check();
                }
            }
        });

        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLocationToTrusted();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (preferences.getString("active", "no").equals("no")) {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("person").child("counter");
                    databaseReference.setValue("0");

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("status");
                    databaseReference2.setValue("active");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("problem-record").child(currentDateandTime);
                    databaseReference3.setValue(preferences.getString("problem-id", "NA"));

                    DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference().child("problem-id").child(preferences.getString("problem-id", "1"));
                    databaseReference4.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("problem-id").child("counter");
                    int a = Integer.parseInt(preferences.getString("problem-id", "1"));

                    a++;


                    databaseReference1.setValue(String.valueOf(a));

                    shareLocationToTrusted();
                    callActionScreen();

                    editor.putString("girl-login", "yes");
                    editor.commit();
                } else {
                    recent_check();
                }
            }
        });

        return root;
    }


    public void callActionScreen() {
        Intent intent = new Intent(getActivity(), action_screen.class);
        startActivity(intent);
    }

    private void shareLocationToTrusted() {

        databaseReference_sms = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Trusted_person")
                .child("Info");

        databaseReference_sms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Toast.makeText(getActivity(), "Location shared Via SMS", Toast.LENGTH_LONG).show();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Trusted_person_model trusted_person_model = ds.getValue(Trusted_person_model.class);
                    String destPhone = trusted_person_model.getContact();
                    sendSMS(destPhone);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendSMS(String destPhone) {

        if (checkSMSpermission() && checkPhoneStatePermission()) {

            Log.d(TAG, "sendSMS: message sent");
            String message = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
            SMS smsObject = new SMS();
            smsObject.sendSMS(destPhone, message);


        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE}, SEND_SMS_PERMISSION_REQUEST);
        }
    }

    public boolean checkSMSpermission() {

        int check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public boolean checkPhoneStatePermission() {

        int check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        return (check == PackageManager.PERMISSION_GRANTED);

    }

    public void new_entry_track() {
        final Dialog dialog = new Dialog(getActivity());
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
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText.onEditorAction(EditorInfo.IME_ACTION_DONE);

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
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
                                        Toast.makeText(getActivity(), "Starting the live Tracking", Toast.LENGTH_LONG).show();
                                        editor.putString("problem-id", editText.getText().toString());
                                        editor.commit();

                                        new_user_info();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Problem is closed", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {

                            Toast.makeText(getActivity(), "Wrong ID, Please Check it", Toast.LENGTH_LONG).show();
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
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.new_user_track_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final EditText name = dialog.findViewById(R.id.new_entry_name);
        final EditText contact = dialog.findViewById(R.id.new_entry_contact);


        final int[] a = new int[1];

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Problem_Record").child(preferences.getString("problem-id", "1")).child("person").child("counter");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                a[0] = dataSnapshot.getValue().hashCode();

                Log.e("hjkiuy", String.valueOf(a[0]));

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

                Intent i = new Intent(getActivity(), MapActivity.class);
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void recent_check() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.recent_action_check);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button cancel = dialog.findViewById(R.id.recent_action_cancel);
        Button ok = dialog.findViewById(R.id.recent_action_ok);

        ok.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), MapActivity.class);
            startActivity(i);
        });

        cancel.setOnClickListener(v -> {
            editor.putString("active", "no");
            editor.commit();
            Toast.makeText(getActivity(), "successfully exit", Toast.LENGTH_LONG).show();
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
    }

    private void requestAudioPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_PERMISSION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage_result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;

    }

    public void startRecording() {

        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                UUID.randomUUID().toString() + "_audio.3gp";

        setupMediaRecorder();
        try {

            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

        Toast.makeText(getActivity(), "Recording...", Toast.LENGTH_SHORT).show();
    }

    public void stopRecording() {
        mediaRecorder.stop();
    }

    private void setupMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }

    private void startShakeService() {
        Intent serviceIntent = new Intent(getActivity(), foreground_service.class);
        serviceIntent.addCategory(tag_service);
        serviceIntent.putExtra("inputExtra", "shake your phone to start the security service");

        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    private void stopShakeService() {
        Intent serviceIntent = new Intent(getActivity(), foreground_service.class);
        serviceIntent.addCategory(tag_service);
        getActivity().stopService(serviceIntent);
    }

}






