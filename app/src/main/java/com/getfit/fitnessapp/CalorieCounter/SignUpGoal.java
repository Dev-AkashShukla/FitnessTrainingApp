package com.getfit.fitnessapp.CalorieCounter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.getfit.fitnessapp.R;

import java.util.Calendar;

public class SignUpGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_goal);

        //Listener
        Button buttonSubmit = (Button) findViewById(R.id.button_sign_up_goal);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpGoalSubmit();
            }
        });

    }

    private void signUpGoalSubmit() {

        DBAdapter db = new DBAdapter(this);
        db.open();

        //Get target weight
        EditText editTextTargetWeight = (EditText) findViewById(R.id.editText_target_weight);
        String stringTargetWeight = editTextTargetWeight.getText().toString();
        double doubleTargetWeight = 0;
        try {
            doubleTargetWeight = Double.parseDouble(stringTargetWeight);
        } catch (NumberFormatException e) {
            Toast.makeText(SignUpGoal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Spinner weeklyGoals
        // 0 - Loose weight
        // 1 - Gain Weight
        Spinner spinnerWeeklyGoals = (Spinner) findViewById(R.id.spinner_weekly_goals);
        int intWeeklyGoals = spinnerWeeklyGoals.getSelectedItemPosition();

        //Spinner weeklyGoalsUnits
        Spinner spinnerWeeklyGoalsUnits = (Spinner) findViewById(R.id.spinner_weekly_goals_units);
        String stringWeeklyGoalsUnits = spinnerWeeklyGoalsUnits.getSelectedItem().toString();

        if (TextUtils.isEmpty(stringTargetWeight)) {
            Toast.makeText(SignUpGoal.this, "Weight is needed", Toast.LENGTH_SHORT).show();
            editTextTargetWeight.setError("Please enter your current weight");
            editTextTargetWeight.requestFocus();
        } else {

            long goalID = 1;

            double doubleTargetWeightSQL = db.quoteSmart(doubleTargetWeight);
            db.update("goal", "_id", goalID, "goal_target_weight", doubleTargetWeightSQL);

            int intIWantToSQL = db.quoteSmart(intWeeklyGoals);
            db.update("goal", "_id", goalID, "goal_i_want_to", intIWantToSQL);

            String stringWeeklyGoalSQL = db.quoteSmart(stringWeeklyGoalsUnits);
            db.update("goal", "_id", goalID, "goal_weekly_goal", stringWeeklyGoalSQL);

            //Calculate energy

            //Get row number one from users
            long rowID = 1;
            String fields[] = new String[] {
                    "_id",
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_activity_level"
            };
            Cursor cursor = db.selectPrimaryKey("users", "_id", rowID, fields);

            String stringUserDob = cursor.getString(1);
            String stringUserGender = cursor.getString(2);
            String stringUserHeight = cursor.getString(3);
            String stringUserActivityLevel = cursor.getString(4);

            int intUserActivityLevel = 0;
            try {
                intUserActivityLevel = Integer.parseInt(stringUserActivityLevel);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
            }

            int intUserActivityLevelSQL = db.quoteSmart(intUserActivityLevel);

            db.update("goal", "_id", goalID, "goal_activity_level", intUserActivityLevelSQL);

            //Get age
            String textSADoB[] = stringUserDob.split("-");

            int year = Integer.parseInt(textSADoB[0]);
            int month = Integer.parseInt(textSADoB[1]);
            int day = Integer.parseInt(textSADoB[2]);
            String stringUserAge = getAge(year, month, day);

            int intUserAge = 0;
            try {
                intUserAge = Integer.parseInt(stringUserAge);
            } catch (NumberFormatException e) {
                Toast.makeText(SignUpGoal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //Height
            double doubleUserHeight = 0;
            try {
                doubleUserHeight = Double.parseDouble(stringUserHeight);
            } catch (NumberFormatException e) {
                Toast.makeText(SignUpGoal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(SignUpGoal.this, stringUserAge, Toast.LENGTH_LONG).show();

            //Start calculation
            double bmr = 0;
            if (stringUserGender.startsWith("m")) {
                // Male
                // BMR = 66.5 + (13.75 * kg body weight) + (5.003 * height in cm) - (6.775 * age)
                bmr = 66.5 + (13.75 * doubleTargetWeight) + (5.003 * doubleUserHeight) - (6.775 * intUserAge);
                bmr = Math.round(bmr);
            } else {
                // Female
                // BMR = 55.1 + (9.563 * kg body weight) + (1.850 * height in cm) - (4.676 * age)
                bmr = 55.1 + (9.563 * doubleTargetWeight) + (1.850 * doubleUserHeight) - (4.676 * intUserAge);
                bmr = Math.round(bmr);
            }


            // 1: BMR
            bmr = Math.round(bmr);
            double energyBmrSQL = db.quoteSmart(bmr);
            db.update("goal", "_id", goalID, "goal_energy_bmr", energyBmrSQL);

            Toast.makeText(SignUpGoal.this, String.valueOf(bmr), Toast.LENGTH_LONG).show();

            // Calculate elements with BMR
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteinsBmr = Math.round(bmr * 0.25);
            double carbsBmr = Math.round(bmr * 0.5);
            double fatBmr = Math.round(bmr * 0.25);

            double proteinsBmrSQL = db.quoteSmart(proteinsBmr);
            double carbsBmrSQL = db.quoteSmart(carbsBmr);
            double fatBmrSQL = db.quoteSmart(fatBmr);

            db.update("goal", "_id", goalID, "goal_proteins_bmr", proteinsBmrSQL);
            db.update("goal", "_id", goalID, "goal_carbs_bmr", carbsBmrSQL);
            db.update("goal", "_id", goalID, "goal_fat_bmr", fatBmrSQL);


            // 2: Diet

            double doubleWeeklyGoal = 0;
            try {
                doubleWeeklyGoal = Double.parseDouble(stringWeeklyGoalsUnits);
            } catch (NumberFormatException e) {
                Toast.makeText(SignUpGoal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // 1 kg fat = 7700 kcal
            double kcal = 0;
            double energyDiet = 0;
            kcal = 7700 * doubleWeeklyGoal;
            if (intWeeklyGoals == 0) {
                // Loose weight
                energyDiet = Math.round((bmr - (kcal / 7)) * 1.2);
            } else {
                // Gain weight
                energyDiet = Math.round((bmr + (kcal / 7)) * 1.2);
            }

            // Update database
            double energyDietSQL = db.quoteSmart(energyDiet);
            db.update("goal", "_id", goalID, "goal_energy_diet", energyDietSQL);

            // Calculate elements with Diet
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteinsDiet = Math.round(energyDiet * 0.25);
            double carbsDiet = Math.round(energyDiet * 0.5);
            double fatDiet = Math.round(energyDiet * 0.25);

            double proteinsDietSQL = db.quoteSmart(proteinsDiet);
            double carbsDietSQL = db.quoteSmart(carbsDiet);
            double fatDietSQL = db.quoteSmart(fatDiet);

            db.update("goal", "_id", goalID, "goal_proteins_diet", proteinsDietSQL);
            db.update("goal", "_id", goalID, "goal_carbs_diet", carbsDietSQL);
            db.update("goal", "_id", goalID, "goal_fat_diet", fatDietSQL);


            // 3: BMR with activity
            // Taking in to account activity
            double energyWithActivity = 0;
            if (stringUserActivityLevel.equals("0")) {
                energyWithActivity = bmr * 1.2;
            } else if (stringUserActivityLevel.equals("1")) {
                energyWithActivity = bmr * 1.375;
            } else if (stringUserActivityLevel.equals("2")) {
                energyWithActivity = bmr * 1.55;
            } else if (stringUserActivityLevel.equals("3")) {
                energyWithActivity = bmr * 1.725;
            } else if (stringUserActivityLevel.equals("4")) {
                energyWithActivity = bmr * 1.9;
            }
            energyWithActivity = Math.round(energyWithActivity);
            double energyWithActivitySQL = db.quoteSmart(energyWithActivity);
            db.update("goal", "_id", goalID, "goal_energy_with_activity", energyWithActivitySQL);

            // Calculate elements with Activity
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteinsWithActivity = Math.round(energyWithActivity * 0.25);
            double carbsWithActivity = Math.round(energyWithActivity * 0.5);
            double fatWithActivity = Math.round(energyWithActivity * 0.25);

            double proteinsWithActivitySQL = db.quoteSmart(proteinsWithActivity);
            double carbsWithActivitySQL = db.quoteSmart(carbsWithActivity);
            double fatWithActivitySQL = db.quoteSmart(fatWithActivity);

            db.update("goal", "_id", goalID, "goal_proteins_with_activity", proteinsWithActivitySQL);
            db.update("goal", "_id", goalID, "goal_carbs_with_activity", carbsWithActivitySQL);
            db.update("goal", "_id", goalID, "goal_fat_with_activity", fatWithActivitySQL);


            // 4: With activity and diet

            // Loose or gain weight?

            // 1 kg fat = 7700 kcal
            kcal = 0;
            double energyWithActivityAndDiet = 0;
            kcal = 7700 * doubleWeeklyGoal;
            if (intWeeklyGoals == 0) {
                // Loose weight
                energyWithActivityAndDiet = Math.round(energyWithActivity - (kcal / 7));
            } else {
                // Gain weight
                energyWithActivityAndDiet = Math.round(energyWithActivity + (kcal / 7));
            }

            // Update database
            double energyWithActivityAndDietSQL = db.quoteSmart(energyWithActivityAndDiet);
            db.update("goal", "_id", goalID, "goal_energy_with_activity_and_diet", energyWithActivityAndDietSQL);

            // Calculate elements
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteins = Math.round(energyWithActivityAndDiet * 0.25);
            double carbs = Math.round(energyWithActivityAndDiet * 0.5);
            double fat = Math.round(energyWithActivityAndDiet * 0.25);

            double proteinsSQL = db.quoteSmart(proteins);
            double carbsSQL = db.quoteSmart(carbs);
            double fatSQL = db.quoteSmart(fat);

            db.update("goal", "_id", goalID, "goal_proteins_with_activity_and_diet", proteinsSQL);
            db.update("goal", "_id", goalID, "goal_carbs_with_activity_and_diet", carbsSQL);
            db.update("goal", "_id", goalID, "goal_fat_with_activity_and_diet", fatSQL);

        }

        db.close();

        Intent intent = new Intent(SignUpGoal.this, DietActivity.class);
        startActivity(intent);
        finish();

    }

    //mesurmentUsed
    public void mesurmentUsed() {
        DBAdapter db = new DBAdapter(this);
        db.open();

        //Get row number one from users
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_mesurment"
        };
        Cursor cursor;
        cursor = db.selectPrimaryKey("users", "_id", rowID, fields);
        String mesurment;
        mesurment = cursor.getString(1);

        Toast.makeText(this, "Mesurment:" + mesurment, Toast.LENGTH_LONG).show();

        if (mesurment.startsWith("m")) {
            //Metric
        } else {
            //Imperial

            //kg to pounds
            TextView textView_target_units_weight = (TextView) findViewById(R.id.textView_target_units_weight);
            textView_target_units_weight.setText("pounds");

            //kg each week to pounds each week
            TextView textView_weakly_goal_units = (TextView) findViewById(R.id.textView_weakly_goal_units);
            textView_weakly_goal_units.setText("pounds each week");

        }

        db.close();
    }

    //getAge
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

}
