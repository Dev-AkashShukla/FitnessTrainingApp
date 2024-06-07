package com.getfit.fitnessapp.CalorieCounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.getfit.fitnessapp.R;
import com.getfit.fitnessapp.SignInUpProfile.ReadWriteUserDetails;
import com.getfit.fitnessapp.SignInUpProfile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignUpDiet extends AppCompatActivity {

    private FirebaseAuth authProfile;

    private EditText editTextHeight, editTextWeight, editTextHeightInches;
    private TextView textViewUnitsHeight, textViewUnitsWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_diet);

        editTextHeight = findViewById(R.id.editText_height);
        editTextWeight = findViewById(R.id.editText_weight);

        textViewUnitsHeight = findViewById(R.id.textView_units_height);
        textViewUnitsWeight = findViewById(R.id.textView_units_weight);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Hide inches field
        editTextHeightInches = findViewById(R.id.editText_height_inches);
        editTextHeightInches.setVisibility(View.GONE);

        //Listener Mesurment spinner
        Spinner spinnerMesurment = (Spinner) findViewById(R.id.spinnerMesurment);
        spinnerMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mesurmentChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                mesurmentChanged();
            }
        });

        Button buttonSignUpDiet = (Button) findViewById(R.id.button_sign_up_diet);
        buttonSignUpDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpDietSubmit(firebaseUser);
            }
        });

    }

    public void mesurmentChanged() {
        String height = editTextHeight.getText().toString();
        String heightInches = editTextHeightInches.getText().toString();
        String weight = editTextWeight.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInch = 0;

        double doubleWeight = 0;

        //Mesurment spinner
        Spinner spinnerMesurment = (Spinner) findViewById(R.id.spinnerMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();
        if (stringMesurment.startsWith("M")) {
            //Metric
            editTextHeightInches.setVisibility(View.GONE);
            textViewUnitsHeight.setTextSize(16);
            textViewUnitsHeight.setText("cm");
            textViewUnitsWeight.setText("kg");

            try {
                heightCm = Double.parseDouble(height);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            try {
                heightInch = Double.parseDouble(heightInches);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            try {
                doubleWeight = Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            if (heightCm != 0 && heightInch != 0) {
                heightFeet = heightCm;
                heightCm = ((heightFeet * 12) + heightInch) * 2.54;
                heightCm = Math.round(heightCm);

                editTextHeight.setText(String.valueOf(heightCm));
            }

            if (doubleWeight != 0) {
                //Pounds to kg
                doubleWeight = Math.round(doubleWeight * 0.45359237);

                editTextWeight.setText(String.valueOf(doubleWeight));
            }

        } else {
            //Imperial
            editTextHeightInches.setVisibility(View.VISIBLE);
            textViewUnitsHeight.setTextSize(8);
            textViewUnitsHeight.setText("feet and inches");
            textViewUnitsWeight.setText("pounds");

            try {
                heightCm = Double.parseDouble(height);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            try {
                doubleWeight = Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            if (heightCm != 0) {

                //Convert cm into feet
                //feet = (cm * 0.3937008) / 12

                heightFeet = (heightCm * 0.3937008) / 12;
//                heightFeet = Math.round(heightFeet);
                int intHeightFeet = (int) heightFeet;

                editTextHeight.setText(String.valueOf(intHeightFeet));
            }

            if (doubleWeight != 0) {
                //Kg to pounds
                doubleWeight = Math.round(doubleWeight / 0.45359237);

                editTextWeight.setText(String.valueOf(doubleWeight));
            }

        }
    }

    public void signUpDietSubmit(FirebaseUser firebaseUser) {
        String height = editTextHeight.getText().toString();
        String heightInches = editTextHeightInches.getText().toString();
        String weight = editTextWeight.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInch = 0;

        double doubleWeight = 0;


        if (TextUtils.isEmpty(height) && editTextHeightInches.getVisibility() == View.GONE) {
            Toast.makeText(SignUpDiet.this, "Height is needed(cm)", Toast.LENGTH_SHORT).show();
            editTextHeight.setError("Please enter your current height(cm)");
            editTextHeight.requestFocus();
        } else if (TextUtils.isEmpty(height) && editTextHeightInches.getVisibility() == View.VISIBLE) {
            Toast.makeText(SignUpDiet.this, "Feet is needed", Toast.LENGTH_SHORT).show();
            editTextHeight.setError("Please enter your current feet");
            editTextHeight.requestFocus();
        } else if (TextUtils.isEmpty(heightInches) && editTextHeightInches.getVisibility() == View.VISIBLE) {
            Toast.makeText(SignUpDiet.this, "Inches is needed", Toast.LENGTH_SHORT).show();
            editTextHeightInches.setError("Please enter your current inches");
            editTextHeightInches.requestFocus();
        } else if (TextUtils.isEmpty(weight)) {
            Toast.makeText(SignUpDiet.this, "Weight is needed", Toast.LENGTH_SHORT).show();
            editTextWeight.setError("Please enter your current weight");
            editTextWeight.requestFocus();
        } else {

            try {
                heightCm = Double.parseDouble(height);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            try {
                heightInch = Double.parseDouble(heightInches);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            try {
                doubleWeight = Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            Spinner spinnerMesurment = (Spinner) findViewById(R.id.spinnerMesurment);
            String stringMesurment = spinnerMesurment.getSelectedItem().toString();

            int intMesurment = spinnerMesurment.getSelectedItemPosition();
            if(intMesurment == 0) {
                stringMesurment = "metric";
            } else {
                stringMesurment = "imperial";
            }

            if (stringMesurment.startsWith("i")) {
                //Feet and inches
                //Need to convert, we want to save the number in cm
                //cm = ((foot * 12) + inches) * 2.54
                heightFeet = heightCm;
                heightCm = ((heightFeet * 12) + heightInch) * 2.54;
                heightCm = Math.round(heightCm);

                //Weight
                //Pounds to kg
                //Pounds to kg
                doubleWeight = Math.round(doubleWeight * 0.45359237);
            }

            //Activity level
            Spinner spinnerActivityLevel = (Spinner) findViewById(R.id.spinner_activity_level);
//        0: Little no exercise</item>
//        1: Light exercise (1-3 days per week)
//        2: Moderate exercise (3-5 days per week)
//        3: Heavy exercise (6-7 days per week)
//        4: Very heavy exercise (twice per day, extra heavy workouts)
            int intActivityLevel = spinnerActivityLevel.getSelectedItemPosition();

            String userIDofRegistered = firebaseUser.getUid();

            //Extracting User Reference from Database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            double finalHeightCm = heightCm;
            double finalDoubleWeight = doubleWeight;
            String finalStringMesurment = stringMesurment;

            referenceProfile.child(userIDofRegistered).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        ReadWriteUserDetails readUserDetails = task.getResult().getValue(ReadWriteUserDetails.class);
                        String email = firebaseUser.getEmail();
                        String textFullName = firebaseUser.getDisplayName();
                        String textDoB = readUserDetails.doB;
                        String textGender = readUserDetails.gender;

                        String textSADoB[] = textDoB.split("-");

                        int day = Integer.parseInt(textSADoB[0]);
                        int month = Integer.parseInt(textSADoB[1]) - 1; //to take care of month index starting from 0
                        int year = Integer.parseInt(textSADoB[2]);

                        String dateOfBirth = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);

                        //Insert into database
                        DBAdapter db = new DBAdapter(SignUpDiet.this);
                        db.open();

                        //Quote smart
                        String stringEmailSQL = db.quoteSmart(email);
                        String dateOfBirthSQL = db.quoteSmart(dateOfBirth);
                        String stringGenderSQL = db.quoteSmart(textGender.toLowerCase());
                        double heightCmSQL = db.quoteSmart(finalHeightCm);
                        int intActivityLevelSQL = db.quoteSmart(intActivityLevel);
                        double doubleWeightSQL = db.quoteSmart(finalDoubleWeight);
                        String stringMesurmentSQL = db.quoteSmart(finalStringMesurment);

                        // Input for users
                        String stringInput = "NULL, " + stringEmailSQL + "," + dateOfBirthSQL + "," +
                                stringGenderSQL + "," + heightCmSQL + "," + intActivityLevelSQL + "," + stringMesurmentSQL;

                        db.insert("users",
                                "_id, user_email, user_dob, user_gender, user_height, user_activity_level, user_mesurment",
                                stringInput);

                        // Input for goal

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String goalDate = dateFormat.format(Calendar.getInstance().getTime());

//                        Calendar cc = Calendar.getInstance();
//                        int goalYear = cc.get(Calendar.YEAR);
//                        int goalMonth = cc.get(Calendar.MONTH);
//                        int goalDay = cc.get(Calendar.DAY_OF_MONTH);
//                        String goalDate = goalYear + "-" + goalMonth + "-" + goalDay;
                        String goalDateSQL = db.quoteSmart(goalDate);

                        stringInput = "NULL, " + doubleWeightSQL + "," + goalDateSQL;

                        db.insert("goal",
                                "_id, goal_current_weight, goal_date",
                                stringInput);


                        db.close();

                        //Move user back to DietActivity

                        Intent intent = new Intent(SignUpDiet.this, SignUpGoal.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
