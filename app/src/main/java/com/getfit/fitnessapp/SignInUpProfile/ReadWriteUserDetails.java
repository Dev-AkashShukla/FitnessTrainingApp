package com.getfit.fitnessapp.SignInUpProfile;

public class ReadWriteUserDetails {
    public String fullName;
    public String doB;
    public String gender;
    public String mobile;

    // Default constructor required for calls to DataSnapshot.getValue(ReadWriteUserDetails.class)
    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String fullName, String doB, String gender, String mobile) {
        this.fullName = fullName;
        this.doB = doB;
        this.gender = gender;
        this.mobile = mobile;
    }
}