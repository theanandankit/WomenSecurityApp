package com.project.womensecurityapp.User_login_info;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.project.womensecurityapp.model.Trusted_person_model;

public class Trusted_person extends AppCompatActivity {
    TextInputLayout name, contact, mail, address, relation;
    Button add, more, proceed;
    DatabaseReference d;
    int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_person);
        name = findViewById(R.id.trusted_name);
        contact = findViewById(R.id.trusted_contact);
        mail = findViewById(R.id.trusted_email);
        address = findViewById(R.id.trusted_address);
        relation = findViewById(R.id.trusted_relation);
        add = findViewById(R.id.trusted_add);

        d = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Trusted_person");

        add.setOnClickListener(v -> {
            if (name.getEditText().getText().toString().isEmpty() || contact.getEditText().getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(), "Name and contact no. is mandatory", Toast.LENGTH_SHORT).show();
            else {
                d.child("Info").child(contact.getEditText().getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Person already exists", Toast.LENGTH_LONG).show();
                            showDialog();
                            /**/
                        } else {
                            Trusted_person_model trusted_person_model = new Trusted_person_model(name.getEditText().getText().toString(), contact.getEditText().getText().toString(), mail.getEditText().getText().toString(), address.getEditText().getText().toString(), relation.getEditText().getText().toString());
                            d.child("Info").child(contact.getEditText().getText().toString()).setValue(trusted_person_model);
                            d.child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int c = Integer.parseInt(dataSnapshot.getValue().toString());
                                    d.child("count").setValue(Integer.toString((c + 1)));
                                    Toast.makeText(getApplicationContext(), String.valueOf(c + 1), Toast.LENGTH_SHORT).show();
                                    showDialog();


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


        });

    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_more);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(true);
        more = dialog.findViewById(R.id.more);
        proceed = dialog.findViewById(R.id.proceed);
        more.setOnClickListener(v -> {
            Intent i = new Intent(Trusted_person.this, Trusted_person.class);
            startActivity(i);
        });
        proceed.setOnClickListener(v -> {
            Intent i = new Intent(Trusted_person.this, Main2Activity.class);
            startActivity(i);

        });
        dialog.show();


    }
}
