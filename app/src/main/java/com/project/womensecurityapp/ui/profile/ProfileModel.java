package com.project.womensecurityapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.womensecurityapp.model.User_residential_details;

public class ProfileModel extends ViewModel {

    private MutableLiveData<String> mName;
    private MutableLiveData<String> mNo;
    private MutableLiveData<String> mEmail;
    private MutableLiveData<String> mCity;
    private MutableLiveData<String> mAddress;
    private MutableLiveData<String> mCountry;
   User_residential_details t;

    public ProfileModel() {
        mName = new MutableLiveData<>();
        mNo = new MutableLiveData<>();
        mEmail = new MutableLiveData<>();
        mCity = new MutableLiveData<>();
        mCountry = new MutableLiveData<>();
        mAddress= new MutableLiveData<>();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Personal_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t=dataSnapshot.getValue(User_residential_details.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public MutableLiveData<String> getmName() {
        mName.setValue(t.getName());
        return mName;
    }

    public void setmName(MutableLiveData<String> mName) {
        this.mName = mName;
    }

    public MutableLiveData<String> getmNo() {
        mNo.setValue(t.getContact_no());
        return mNo;
    }

    public void setmNo(MutableLiveData<String> mNo) {
        this.mNo = mNo;
    }

    public MutableLiveData<String> getmEmail() {
        mEmail.setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        return mEmail;
    }

    public void setmEmail(MutableLiveData<String> mEmail) {
        this.mEmail = mEmail;
    }

    public MutableLiveData<String> getmCity() {
        mCity.setValue(t.getCity());
        return mCity;
    }

    public void setmCity(MutableLiveData<String> mCity) {
        this.mCity = mCity;
    }

    public MutableLiveData<String> getmAddress() {
        mAddress.setValue(t.getHouse_no()+","+t.getStreet());
        return mAddress;
    }

    public void setmAddress(MutableLiveData<String> mAddress) {
        this.mAddress = mAddress;
    }

    public MutableLiveData<String> getmCountry() {
        mCountry.setValue(t.getCountry());
        return mCountry;
    }

    public void setmCountry(MutableLiveData<String> mCountry) {
        this.mCountry = mCountry;
    }

    public User_residential_details getT() {
        return t;
    }

    public void setT(User_residential_details t) {
        this.t = t;
    }
}