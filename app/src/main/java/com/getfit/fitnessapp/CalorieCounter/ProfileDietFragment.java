package com.getfit.fitnessapp.CalorieCounter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.getfit.fitnessapp.R;
import com.getfit.fitnessapp.SignInUpProfile.UserProfileActivity;


public class ProfileDietFragment extends Fragment {

    private View mainView;

    //Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileDietFragment() {
        // Required empty public constructor
    }

    public static ProfileDietFragment newInstance(String param1, String param2) {
        ProfileDietFragment fragment = new ProfileDietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        //getDataFromDbAndDisplay
        initializeGetDataFromDbAndDisplay();

        //Create menu
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_profile_diet, container, false);
        return mainView;
    }

    //set Main View
    private void setMainView(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    //on Create Options Menu
    //Creating action icon on toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Inflate menu
        MenuInflater menuInflater = ((DietActivity)getActivity()).getMenuInflater();
//        inflater.inflate(R.menu.menu_goal, menu);

        // Assign menu items to variables
        menuItemEdit = menu.findItem(R.id.menu_action_food_edit);
//        menuItemDelete = menu.findItem(R.id.menu_action_food_delete);
//
//        //Hide as default
//        menuItemEdit.setVisible(false);
//        menuItemDelete.setVisible(false);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.menu_action_food_add) {
//            addFood();
//        }
//        if (id == R.id.menu_action_food_edit) {
//            editFood();
//        }
//        if (id == R.id.menu_action_food_delete) {
//            deleteFood();
//        }

        return super.onOptionsItemSelected(item);
    }

    //Our own methods

    //Get data from db and display
    public void initializeGetDataFromDbAndDisplay() {

        //Get data from db
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get row number one from users
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_dob",
                "user_gender",
                "user_height",
                "user_mesurment"
        };
        Cursor c = db.select("users", fields, "_id", rowID);
        String stringUserDob = c.getString(1);
        String stringUserGender = c.getString(2);
        String stringUserHeight = c.getString(3);
        String stringUserMesurment = c.getString(4);

        //Height
        EditText editTextProfileHeightCm = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeight);
        EditText editTextProfileHeightInches = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeightInches);
        TextView textViewEditProfileCm = (TextView) getActivity().findViewById(R.id.textView_units_height);

        if (stringUserMesurment.startsWith("m")) {
            //Metric
            editTextProfileHeightInches.setVisibility(View.GONE);
            editTextProfileHeightCm.setText(stringUserHeight);
        } else {
            textViewEditProfileCm.setText("feet and inches");
            double heightCm = 0;
            double heightFeet = 0;
            double heightInches = 0;

            try {
                heightCm = Double.parseDouble(stringUserHeight);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
            if (heightCm != 0) {
                //Convert cm into feet
                //feet = (cm * 0.3937008) / 12
                heightFeet = (heightCm * 0.3937008) / 12;
                //heightFeet = Math.round(heightFeet);
                int intHeightFeet = (int) heightFeet;

                editTextProfileHeightCm.setText(String.valueOf(intHeightFeet));
            }
        }

        //Mesurment
        Spinner spinnerEditProfileMesurment = (Spinner) getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        if (stringUserMesurment.startsWith("m")) {
            spinnerEditProfileMesurment.setSelection(0);
        }
        else {
            spinnerEditProfileMesurment.setSelection(1);
        }

        //Listener Mesurment spinner
        spinnerEditProfileMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mesurmentChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Listener buttonSave
        Button buttonEditProfileSubmit = (Button) getActivity().findViewById(R.id.buttonEditProfileSubmit);
        buttonEditProfileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileSubmit();
            }
        });

        db.close();

    }

    public void mesurmentChanged() {
        double heightCm = 0;
        double heightFeet = 0;
        double heightInch = 0;

        //Mesurment spinner
        Spinner spinnerMesurment = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileMesurment);
        String stringMesurment = spinnerMesurment.getSelectedItem().toString();

        //Height
        EditText editTextProfileHeightCm = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeight);
        EditText editTextProfileHeightInches = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeightInches);

        String height = editTextProfileHeightCm.getText().toString();
        String heightInches = editTextProfileHeightInches.getText().toString();

        TextView textViewEditProfileCm = (TextView) getActivity().findViewById(R.id.textView_units_height);

        if (stringMesurment.startsWith("M")) {
            //Metric
            editTextProfileHeightInches.setVisibility(View.GONE);
            textViewEditProfileCm.setTextSize(16);
            textViewEditProfileCm.setText("cm");

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

            if (heightCm != 0 && heightInch != 0) {
                heightFeet = heightCm;
                heightCm = ((heightFeet * 12) + heightInch) * 2.54;
                heightCm = Math.round(heightCm);

                editTextProfileHeightCm.setText(String.valueOf(heightCm));
            }
        } else {
            //Imperial
            editTextProfileHeightInches.setVisibility(View.VISIBLE);
            textViewEditProfileCm.setTextSize(8);
            textViewEditProfileCm.setText("feet and inches");

            try {
                heightCm = Double.parseDouble(height);
            } catch (NumberFormatException e) {
                System.out.println(e);
            }

            if (heightCm != 0) {

                //Convert cm into feet
                //feet = (cm * 0.3937008) / 12

                heightFeet = (heightCm * 0.3937008) / 12;
//                heightFeet = Math.round(heightFeet);
                int intHeightFeet = (int) heightFeet;

                editTextProfileHeightCm.setText(String.valueOf(intHeightFeet));
            }
        }
    }

    //Edit profile submit
    public void editProfileSubmit() {

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Height
        EditText editTextProfileHeightCm = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeight);
        EditText editTextProfileHeightInches = (EditText) getActivity().findViewById(R.id.editTextProfileDietHeightInches);
        String stringHeightCm = editTextProfileHeightCm.getText().toString();
        String stringHeightInches = editTextProfileHeightInches.getText().toString();

        double heightCm = 0;
        double heightFeet = 0;
        double heightInch = 0;


        if (TextUtils.isEmpty(stringHeightCm) && editTextProfileHeightInches.getVisibility() == View.GONE) {
            Toast.makeText(getActivity(), "Height is needed(cm)", Toast.LENGTH_SHORT).show();
            editTextProfileHeightCm.setError("Please enter your current height(cm)");
            editTextProfileHeightCm.requestFocus();
        } else if (TextUtils.isEmpty(stringHeightCm) && editTextProfileHeightInches.getVisibility() == View.VISIBLE) {
            Toast.makeText(getActivity(), "Feet is needed", Toast.LENGTH_SHORT).show();
            editTextProfileHeightCm.setError("Please enter your current feet");
            editTextProfileHeightCm.requestFocus();
        } else if (TextUtils.isEmpty(stringHeightInches) && editTextProfileHeightInches.getVisibility() == View.VISIBLE) {
            Toast.makeText(getActivity(), "Inches is needed", Toast.LENGTH_SHORT).show();
            editTextProfileHeightInches.setError("Please enter your current inches");
            editTextProfileHeightInches.requestFocus();
        } else {

            try {
                heightCm = Double.parseDouble(stringHeightCm);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            try {
                heightInch = Double.parseDouble(stringHeightInches);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }


            Spinner spinnerMesurment = (Spinner)getActivity().findViewById(R.id.spinnerEditProfileMesurment);
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

            }

            stringHeightCm = "" + heightCm;
            String heightCmSQL = db.quoteSmart(stringHeightCm);
            String stringMesurmentSQL = db.quoteSmart(stringMesurment);

            long id = 1;

            String fields[] = new String[] {
                    "user_height",
                    "user_mesurment"
            };
            String values[] = new String[] {
                    heightCmSQL,
                    stringMesurmentSQL
            };

            db.update("users", "_id", id, fields, values);

            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

        }

    }




}