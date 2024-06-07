package com.getfit.fitnessapp.CalorieCounter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.getfit.fitnessapp.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalDietFragment#newInstance} factory method to
 * {@link GoalDietFragment}
 * create an instance of this fragment.
 */
public class GoalDietFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private View mainView;

    //Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    //Holder for buttons on toolbar
    private String currentId;
    private String currentName;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public GoalDietFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GoalDietFragment newInstance(String param1, String param2) {
        GoalDietFragment fragment = new GoalDietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //set Main View
    private void setMainView(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Goal");

        //getDataFromDbAndDisplay
        initializeGetDataFromDbAndDisplay();

        //Create menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        initializeGetDataFromDbAndDisplay();

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_goal_diet, container, false);
        return mainView;
    }

    //on Create Options Menu
    //Creating action icon on toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Inflate menu
        ((DietActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_goal, menu);

        // Assign menu items to variables
        menuItemEdit = menu.findItem(R.id.menu_action_goal_edit);
//        menuItemDelete = menu.findItem(R.id.menu_action_food_delete);
//
//        //Hide as default
//        menuItemEdit.setVisible(false);
//        menuItemDelete.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_action_goal_edit) {
            goalEdit();
        }
        return super.onOptionsItemSelected(item);
    }

    //Our own methods

    //Get data from db and display
    public void initializeGetDataFromDbAndDisplay() {

        //Get data from database

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get row number one from users
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_mesurment"
        };
        Cursor c = db.select("users", fields, "_id", rowID);
        String mesurment = c.getString(1);

        //Get Goal data
        String fieldsGoal[] = new String[] {
                "_id",
                "goal_current_weight",
                "goal_target_weight",
                "goal_i_want_to",
                "goal_weekly_goal",
                "goal_activity_level",
                "goal_date",
        };
        Cursor goalCursor = db.select("goal", fieldsGoal, "", "", "_id", "DESC");

        //Ready as variables
        String goalID = goalCursor.getString(0);
        String goalCurrentWeight = goalCursor.getString(1);
        String goalTargetWeight = goalCursor.getString(2);
        String goalIWantTo = goalCursor.getString(3);
        String goalWeeklyGoal = goalCursor.getString(4);
        String goalActivityLevel = goalCursor.getString(5);
        String goalDate = goalCursor.getString(6);

        //Status

        //Current weight
        TextView textViewGoalCurrentWeightNumber = (TextView)getActivity().findViewById(R.id.textViewGoalCurrentWeightNumber);
        if (mesurment.startsWith("m")) {
            //Metric
            textViewGoalCurrentWeightNumber.setText(goalCurrentWeight + " kg (" + goalDate + ")");
        } else {
            //Imperial
            //Kg to pounds
            double currentWeightNumber = 0;

            try {
                currentWeightNumber = Double.parseDouble(goalCurrentWeight);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            double currentWeightNumberPounds = Math.round(currentWeightNumber / 0.45359237);

            textViewGoalCurrentWeightNumber.setText(currentWeightNumberPounds + " pounds (" + goalDate + ")");
        }

        //Target
        TextView textViewGoalCurrentTargetNumber = (TextView)getActivity().findViewById(R.id.textViewGoalCurrentTargetNumber);
        if (mesurment.startsWith("m")) {
            //Metric
            textViewGoalCurrentTargetNumber.setText(goalTargetWeight + " kg");
        } else {
            //Imperial
            //Kg to pounds
            double targetWeightNumber = 0;

            try {
                targetWeightNumber = Double.parseDouble(goalTargetWeight);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            double targetWeightNumberPounds = Math.round(targetWeightNumber / 0.45359237);

            textViewGoalCurrentTargetNumber.setText(targetWeightNumberPounds + " pounds");
        }

        //Method
        TextView textViewGoalMethodText = (TextView)getActivity().findViewById(R.id.textViewGoalMethodText);

        String method = "";
        if (goalIWantTo.equals("0")) {
            method = "Loose " + goalWeeklyGoal;
        } else {
            method = "Gain " + goalWeeklyGoal;
        }
        if (mesurment.startsWith("m")) {
            method = method + " kg/week";
        } else {
            method = method + " pounds/week";
        }
        textViewGoalMethodText.setText(method);

        //Activity level
        TextView textViewActivityLevel = (TextView) getActivity().findViewById(R.id.textViewActivityLevel);
        if(goalActivityLevel.equals("0")) {
            textViewActivityLevel.setText("Little no exercise");
        } else if (goalActivityLevel.equals("1")) {
            textViewActivityLevel.setText("Light exercise (1-3 days per week)");
        } else if (goalActivityLevel.equals("2")) {
            textViewActivityLevel.setText("Moderate exercise (3-5 days per week)");
        } else if (goalActivityLevel.equals("3")) {
            textViewActivityLevel.setText("Heavy exercise (6-7 days per week)");
        } else if (goalActivityLevel.equals("4")) {
            textViewActivityLevel.setText("Very heavy exercise (twice per day, extra heavy workouts)");
        }

        //Numbers
        updateNumberTable();

        //Hide fields
        toggleNumbersViewGoal(false);

        //Checkbox toogle
        CheckBox checkBoxAdvanced = (CheckBox) getActivity().findViewById(R.id.checkBoxGoalToggle);

        checkBoxAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleNumbersViewGoal(isChecked);
            }
        });

        db.close();

    }

    public void toggleNumbersViewGoal (boolean isChecked) {

        //Remove table rows
//        TableRow textViewGoalMethodRowA = (TableRow) getActivity().findViewById(R.id.textViewGoalMethodRowA);
//
//        TableRow textViewGoalMethodRowB = (TableRow) getActivity().findViewById(R.id.textViewGoalMethodRowB);

        //Hide fields
        TextView textViewGoalHeadcellEnergy = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellEnergy);
        TextView textViewGoalHeadcellProteins = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellProteins);
        TextView textViewGoalHeadcellCarbs = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellCarbs);
        TextView textViewGoalHeadcellFat = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellFat);

        TextView textViewGoalHeadcellEnergyKeep = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellEnergyKeep);
        TextView textViewGoalHeadcellProteinsKeep = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellProteinsKeep);
        TextView textViewGoalHeadcellCarbsKeep = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellCarbsKeep);
        TextView textViewGoalHeadcellFatKeep = (TextView) getActivity().findViewById(R.id.textViewGoalHeadcellFatKeep);

        TextView textViewGoalProteinsBMR = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsBMR);
        TextView textViewGoalCarbsBMR = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsBMR);
        TextView textViewGoalFatBMR = (TextView) getActivity().findViewById(R.id.textViewGoalFatBMR);

        TextView textViewGoalProteinsDiet = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsDiet);
        TextView textViewGoalCarbsDiet = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsDiet);
        TextView textViewGoalFatDiet = (TextView) getActivity().findViewById(R.id.textViewGoalFatDiet);

        TextView textViewGoalProteinsWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsWithActivity);
        TextView textViewGoalCarbsWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsWithActivity);
        TextView textViewGoalFatWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalFatWithActivity);

        TextView textViewGoalProteinsWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsWithActivityAndDiet);
        TextView textViewGoalCarbsWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsWithActivityAndDiet);
        TextView textViewGoalFatWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalFatWithActivityAndDiet);

        if (isChecked == false) {
//            textViewGoalMethodRowA.setVisibility(View.GONE);
//            textViewGoalMethodRowB.setVisibility(View.GONE);
            textViewGoalHeadcellEnergy.setVisibility(View.GONE);
            textViewGoalHeadcellProteins.setVisibility(View.GONE);
            textViewGoalHeadcellCarbs.setVisibility(View.GONE);
            textViewGoalHeadcellFat.setVisibility(View.GONE);
            textViewGoalHeadcellEnergyKeep.setVisibility(View.GONE);
            textViewGoalHeadcellProteinsKeep.setVisibility(View.GONE);
            textViewGoalHeadcellCarbsKeep.setVisibility(View.GONE);
            textViewGoalHeadcellFatKeep.setVisibility(View.GONE);
            textViewGoalProteinsBMR.setVisibility(View.GONE);
            textViewGoalCarbsBMR.setVisibility(View.GONE);
            textViewGoalFatBMR.setVisibility(View.GONE);
            textViewGoalProteinsDiet.setVisibility(View.GONE);
            textViewGoalCarbsDiet.setVisibility(View.GONE);
            textViewGoalFatDiet.setVisibility(View.GONE);
            textViewGoalProteinsWithActivity.setVisibility(View.GONE);
            textViewGoalCarbsWithActivity.setVisibility(View.GONE);
            textViewGoalFatWithActivity.setVisibility(View.GONE);
            textViewGoalProteinsWithActivityAndDiet.setVisibility(View.GONE);
            textViewGoalCarbsWithActivityAndDiet.setVisibility(View.GONE);
            textViewGoalFatWithActivityAndDiet.setVisibility(View.GONE);
        } else {
//            textViewGoalMethodRowA.setVisibility(View.VISIBLE);
//            textViewGoalMethodRowB.setVisibility(View.VISIBLE);
            textViewGoalHeadcellEnergy.setVisibility(View.VISIBLE);
            textViewGoalHeadcellProteins.setVisibility(View.VISIBLE);
            textViewGoalHeadcellCarbs.setVisibility(View.VISIBLE);
            textViewGoalHeadcellFat.setVisibility(View.VISIBLE);
            textViewGoalHeadcellEnergyKeep.setVisibility(View.VISIBLE);
            textViewGoalHeadcellProteinsKeep.setVisibility(View.VISIBLE);
            textViewGoalHeadcellCarbsKeep.setVisibility(View.VISIBLE);
            textViewGoalHeadcellFatKeep.setVisibility(View.VISIBLE);
            textViewGoalProteinsBMR.setVisibility(View.VISIBLE);
            textViewGoalCarbsBMR.setVisibility(View.VISIBLE);
            textViewGoalFatBMR.setVisibility(View.VISIBLE);
            textViewGoalProteinsDiet.setVisibility(View.VISIBLE);
            textViewGoalCarbsDiet.setVisibility(View.VISIBLE);
            textViewGoalFatDiet.setVisibility(View.VISIBLE);
            textViewGoalProteinsWithActivity.setVisibility(View.VISIBLE);
            textViewGoalCarbsWithActivity.setVisibility(View.VISIBLE);
            textViewGoalFatWithActivity.setVisibility(View.VISIBLE);
            textViewGoalProteinsWithActivityAndDiet.setVisibility(View.VISIBLE);
            textViewGoalCarbsWithActivityAndDiet.setVisibility(View.VISIBLE);
            textViewGoalFatWithActivityAndDiet.setVisibility(View.VISIBLE);
        }
    }

    //Goal edit
    public void goalEdit() {

        //Initialize fragment
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = GoalEditDietFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Need to pass meal number
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

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

    //Update table
    private void updateNumberTable() {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get Goal data
        String fieldsGoal[] = new String[] {
                " goal_energy_bmr",
                " goal_proteins_bmr",
                " goal_carbs_bmr",
                " goal_fat_bmr",
                " goal_energy_diet",
                " goal_proteins_diet",
                " goal_carbs_diet",
                " goal_fat_diet",
                " goal_energy_with_activity",
                " goal_proteins_with_activity",
                " goal_carbs_with_activity",
                " goal_fat_with_activity",
                " goal_energy_with_activity_and_diet",
                " goal_proteins_with_activity_and_diet",
                " goal_carbs_with_activity_and_diet",
                " goal_fat_with_activity_and_diet"
        };

        Cursor goalCursor = db.select("goal", fieldsGoal, "", "", "_id", "DESC");

        //Ready as variables
        String goalEnergyBmr = goalCursor.getString(0);
        String goalProteinsBmr = goalCursor.getString(1);
        String goalCarbsBmr = goalCursor.getString(2);
        String goalFatBmr = goalCursor.getString(3);
        String goalEnergyDiet = goalCursor.getString(4);
        String goalProteinsDiet = goalCursor.getString(5);
        String goalCarbsDiet = goalCursor.getString(6);
        String goalFatDiet = goalCursor.getString(7);
        String goalEnergyWithActivity = goalCursor.getString(8);
        String goalProteinsWithActivity = goalCursor.getString(9);
        String goalCarbsWithActivity = goalCursor.getString(10);
        String goalFatWithActivity = goalCursor.getString(11);
        String goalEnergyWithActivityAndDiet = goalCursor.getString(12);
        String goalProteinsWithActivityAndDiet = goalCursor.getString(13);
        String goalCarbsWithActivityAndDiet = goalCursor.getString(14);
        String goalFatWithActivityAndDiet = goalCursor.getString(15);

        //Numbers

        //1 Diet
        TextView textViewGoalEnergyDiet = (TextView) getActivity().findViewById(R.id.textViewGoalEnergyDiet);
        textViewGoalEnergyDiet.setText(goalEnergyDiet);
        TextView textViewGoalProteinsDiet = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsDiet);
        textViewGoalProteinsDiet.setText(goalProteinsDiet);
        TextView textViewGoalCarbsDiet = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsDiet);
        textViewGoalCarbsDiet.setText(goalCarbsDiet);
        TextView textViewGoalFatDiet = (TextView) getActivity().findViewById(R.id.textViewGoalFatDiet);
        textViewGoalFatDiet.setText(goalFatDiet);

        //2 WithActivityAndDiet
        TextView textViewGoalEnergyWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalEnergyWithActivityAndDiet);
        textViewGoalEnergyWithActivityAndDiet.setText(goalEnergyWithActivityAndDiet);
        TextView textViewGoalProteinsWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsWithActivityAndDiet);
        textViewGoalProteinsWithActivityAndDiet.setText(goalProteinsWithActivityAndDiet);
        TextView textViewGoalCarbsWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsWithActivityAndDiet);
        textViewGoalCarbsWithActivityAndDiet.setText(goalCarbsWithActivityAndDiet);
        TextView textViewGoalFatWithActivityAndDiet = (TextView) getActivity().findViewById(R.id.textViewGoalFatWithActivityAndDiet);
        textViewGoalFatWithActivityAndDiet.setText(goalFatWithActivityAndDiet);

        //3 BMR
        TextView textViewGoalEnergyBMR = (TextView) getActivity().findViewById(R.id.textViewGoalEnergyBMR);
        textViewGoalEnergyBMR.setText(goalEnergyBmr);
        TextView textViewGoalProteinsBMR = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsBMR);
        textViewGoalProteinsBMR.setText(goalProteinsBmr);
        TextView textViewGoalCarbsBMR = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsBMR);
        textViewGoalCarbsBMR.setText(goalCarbsBmr);
        TextView textViewGoalFatBMR = (TextView) getActivity().findViewById(R.id.textViewGoalFatBMR);
        textViewGoalFatBMR.setText(goalFatBmr);

        //4 WithActivity
        TextView textViewGoalEnergyWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalEnergyWithActivity);
        textViewGoalEnergyWithActivity.setText(goalEnergyWithActivity);
        TextView textViewGoalProteinsWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalProteinsWithActivity);
        textViewGoalProteinsWithActivity.setText(goalProteinsWithActivity);
        TextView textViewGoalCarbsWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalCarbsWithActivity);
        textViewGoalCarbsWithActivity.setText(goalCarbsWithActivity);
        TextView textViewGoalFatWithActivity = (TextView) getActivity().findViewById(R.id.textViewGoalFatWithActivity);
        textViewGoalFatWithActivity.setText(goalFatWithActivity);

        db.close();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}