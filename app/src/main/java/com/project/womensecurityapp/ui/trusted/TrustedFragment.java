package com.project.womensecurityapp.ui.trusted;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.R;
import com.project.womensecurityapp.User_login_info.Trusted_person;
import com.project.womensecurityapp.model.Trusted_person_model;

import java.util.ArrayList;
import java.util.List;

public class TrustedFragment extends Fragment {

    private static final String TAG = "TrustedFragment";

    private TrustedViewModel trustedViewModel;

    private TrustedPersonAdapter trustedPersonAdapter;
    private List<Trusted_person_model> trustedPersonModel;
    private RecyclerView recyclerView;
    FloatingActionButton fab_addTrusted;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        trustedViewModel = new ViewModelProvider(this).get(TrustedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trusted, container, false);
        trustedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        recyclerView = root.findViewById(R.id.trusted_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab_addTrusted = root.findViewById(R.id.trusted_addPerson);
        fab_addTrusted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTrustedPerson();
            }
        });

        trustedPersonModel = new ArrayList<>();
        getAllTrustedPerson();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder
                    , @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                // delete trusted person

            }
        }).attachToRecyclerView(recyclerView);

        return root;
    }

    private void getAllTrustedPerson() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Trusted_person")
                .child("Info");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                trustedPersonModel.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Trusted_person_model model = ds.getValue(Trusted_person_model.class);
                    trustedPersonModel.add(model);
                    trustedPersonAdapter = new TrustedPersonAdapter(getActivity(), trustedPersonModel);
                    recyclerView.setAdapter(trustedPersonAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void AddTrustedPerson() {

        Intent intent = new Intent(getActivity(), Trusted_person.class);
        startActivity(intent);
    }

}