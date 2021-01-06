package com.project.womensecurityapp.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.Report.AdapterReport;
import com.project.womensecurityapp.Report.ModelReport;

import java.util.ArrayList;
import java.util.List;

import com.project.womensecurityapp.R;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";

    private HistoryViewModel historyViewModel;
    private RecyclerView recyclerView;
    private AdapterReport adapterReport;
    List<ModelReport> modelReportList;
    DatabaseReference databaseReferenceReport;
    DatabaseReference dbRefProblem;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        spinner = root.findViewById(R.id.history_spinner);
        recyclerView = root.findViewById(R.id.history_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerDataList);

        dbRefProblem = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("problem-record");

        spinner.setAdapter(adapter);
        retrievePastProblemId();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String pID = spinner.getSelectedItem().toString();
                modelReportList = new ArrayList<>();
                databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Problem_Record")
                        .child(pID).child("Report");
                makeTimelineReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
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

                    adapterReport = new AdapterReport(getActivity(), modelReportList);
                    recyclerView.setAdapter(adapterReport);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, "makeTimelineReport: Something went wrong");
            }
        });

    }

    public void retrievePastProblemId() {

        dbRefProblem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String id = ds.getValue(String.class);
                    spinnerDataList.add(id);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}