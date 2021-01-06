package com.project.womensecurityapp.User_login_info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.Main2Activity;
import com.project.womensecurityapp.R;
import com.project.womensecurityapp.model.User_residential_details;

public class Account_setup extends AppCompatActivity {

    Intent i;
    TextInputLayout name, contact, house, street, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);


        name = findViewById(R.id.account_name);
        contact = findViewById(R.id.account_phone_no);
        house = findViewById(R.id.account_house_no);
        street = findViewById(R.id.account_street_name);
        city = findViewById(R.id.account_city_name);
        Button submit = findViewById(R.id.account_submit);
        i = getIntent();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                User_residential_details profile = new User_residential_details(name.getEditText().getText().toString(), mail, contact.getEditText().getText().toString(), "India", city.getEditText().getText().toString(), street.getEditText().getText().toString(), house.getEditText().getText().toString());

                Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();


               /* profile.setName(name.getEditText().getText().toString());
                profile.setContact_no(contact.getEditText().getText().toString());
                profile.setHouse_no(house.getEditText().getText().toString());
                profile.setStreet(street.getEditText().getText().toString());
                profile.setCity(city.getEditText().getText().toString());
                profile.setCountry("India");
*/

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.child("Personal_info").setValue(profile);
                FirebaseDatabase.getInstance().getReference().child("City-Records").child(city.getEditText().getText().toString().toUpperCase()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(contact.getEditText().getText().toString());
                databaseReference.child("Trusted_person").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            databaseReference.child("Trusted_person").child("count").setValue("0");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Intent j;
                if (i.getIntExtra("edit", 0) == 1) {
                    j = new Intent(Account_setup.this, Main2Activity.class);
                } else {
                    j = new Intent(Account_setup.this, Trusted_person.class);
                }
                startActivity(j);
            }
        });

    }

    @Override
    protected void onStart() {
        if (i.getIntExtra("edit", 0) == 1) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.child("Personal_info").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User_residential_details profile = dataSnapshot.getValue(User_residential_details.class);
                    String n = profile.getName();
                    String c = profile.getContact_no();
                    String ci = profile.getCity();
                    String cou = profile.getCountry();
                    String hou = profile.getHouse_no();
                    String str = profile.getStreet();
                    String email = profile.getEmail();
                    name.getEditText().setText(n);
                    contact.getEditText().setText(c);
                    city.getEditText().setText(ci);
                    house.getEditText().setText(hou);
                    street.getEditText().setText(str);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        super.onStart();
    }
}
