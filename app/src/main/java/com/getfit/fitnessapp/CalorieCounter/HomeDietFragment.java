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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.getfit.fitnessapp.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeDietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeDietFragment extends Fragment {

    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    //Action buttons on toolbar
    private MenuItem menuItemAddFood;
    private MenuItem menuItemDelete;

    //Holder
    private String currentMealNumber;
    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;

    private String currentFdId;

    private String currentPortionSizePcs;
    private String currentPortionSizeGram;
    private boolean lockPortionSizeByPcs;
    private boolean lockPortionSizeByGram;

    //Holding variables
    private String currentDateYear = "";
    private String currentDateMonth = "";
    private String currentDateDay = "";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeDietFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeDietFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeDietFragment newInstance(String param1, String param2) {
        HomeDietFragment fragment = new HomeDietFragment();
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
        ((DietActivity) getActivity()).getSupportActionBar().setTitle("Home");

        //getDataFromDbAndDisplay
        initializeHome();

        //Create menu
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_home_diet, container, false);
        return mainView;
    }

    //on Create Options Menu
    //Creating action icon on toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Inflate menu
        ((DietActivity) getActivity()).getMenuInflater().inflate(R.menu.menu_home, menu);

        // Assign menu items to variables
        menuItemAddFood = menu.findItem(R.id.menu_action_add_food);
//        menuItemDelete = menu.findItem(R.id.menu_action_food_delete);
//
//        //Hide as default
//        menuItemEdit.setVisible(false);
//        menuItemDelete.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_action_add_food) {
            //addFoodToDiarySelectMealNumber();
        }
        return super.onOptionsItemSelected(item);
    }

    //Our own methods

    //Initialize home
    private void initializeHome() {

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Find date
        if (currentDateYear.equals("") || currentDateMonth.equals("") || currentDateDay.equals("")) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            currentDateYear = "" + year;

            //Month
            month = month + 1; //Month starts with 0
            if (month < 10) {
                currentDateMonth = "0" + month;
            } else {
                currentDateMonth = "" + month;
            }

            //Day
            if (day < 10) {
                currentDateDay = "0" + day;
            } else {
                currentDateDay = "" + day;
            }

        }
        String stringFdDate = currentDateYear + "-" + currentDateMonth + "-" + currentDateDay;


        //Fill table
        updateTableItems(stringFdDate, "0");
        updateTableItems(stringFdDate, "1");
        updateTableItems(stringFdDate, "2");
        updateTableItems(stringFdDate, "3");
        updateTableItems(stringFdDate, "4");
        updateTableItems(stringFdDate, "5");
        updateTableItems(stringFdDate, "6");

        //Calculate number of calories today
        calculateNumberOfCalEatenToday(stringFdDate);

        //Breakfast listener
        ImageView imageViewAddBreakfast = (ImageView) getActivity().findViewById(R.id.imageViewBreakfast);
        imageViewAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(0); //0 == Breakfast
            }
        });

        //Breakfast listener
        ImageView imageViewAddLunch = (ImageView) getActivity().findViewById(R.id.imageViewLunch);
        imageViewAddLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(1);
            }
        });

        //Breakfast listener
        ImageView imageViewAddBeforeTraining = (ImageView) getActivity().findViewById(R.id.imageViewBeforeTraining);
        imageViewAddBeforeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(2);
            }
        });

        //Breakfast listener
        ImageView imageViewAddAfterTraining = (ImageView) getActivity().findViewById(R.id.imageViewAfterTraining);
        imageViewAddAfterTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(3);
            }
        });

        //Breakfast listener
        ImageView imageViewAddDinner = (ImageView) getActivity().findViewById(R.id.imageViewDinner);
        imageViewAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(4);
            }
        });

        //Breakfast listener
        ImageView imageViewAddSnacks = (ImageView) getActivity().findViewById(R.id.imageViewSnacks);
        imageViewAddSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(5);
            }
        });

        //Breakfast listener
        ImageView imageViewAddSupper = (ImageView) getActivity().findViewById(R.id.imageViewSupper);
        imageViewAddSupper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(6);
            }
        });

        db.close();

    }

    //Update table
    public void updateTableItems(String stringDate, String stringMealNumber) {

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        String stringDateSQL = db.quoteSmart(stringDate);

        String stringMealNumberSQL = db.quoteSmart(stringMealNumber);

        //Select
        String fdFields[] = new String[]{
                " _id",
                " fd_food_id",
                " fd_serving_size_gram",
                " fd_serving_size_gram_mesurment",
                " fd_serving_size_pcs",
                " fd_serving_size_pcs_mesurment",
                " fd_energy_calculated",
                " fd_protein_calculated",
                " fd_carbohydrates_calculated",
                " fd_fat_calculated"
        };

        String fdWhereClause[] = new String[]{
                "fd_date",
                "fd_meal_number"
        };

        String fdWhereCondition[] = new String[]{
                stringDateSQL,
                stringMealNumberSQL
        };

        String fdWhereAndOr[] = new String[]{
                "AND"
        };

        Cursor cursorFd = db.select("food_diary", fdFields, fdWhereClause, fdWhereCondition, fdWhereAndOr);

        //Select for food name
        String fieldsFood[] = new String[]{
                " _id",
                " food_name",
                " food_manufactor_name"
        };
        Cursor cursorFood;

        //Select for food_diary_cal_eaten
        Cursor cursorFdce;
        String fieldsFdce[] = new String[]{
                "_id",
                "fdce_id",
                "fdce_meal_no",
                "fdce_energy",
                "fdce_proteins",
                "fdce_carbs",
                "fdce_fat"
        };

        String whereClause[] = new String[]{
                "fdce_date",
                "fdce_meal_no"
        };

        String whereCondition[] = new String[]{
                stringDateSQL,
                stringMealNumberSQL
        };

        String whereAndOr[] = new String[]{
                "AND"
        };

        //cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, "fdce_date", stringDateSQL);
        cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, whereClause, whereCondition, whereAndOr);
        int cursorFdceCount = cursorFdce.getCount();

        if (cursorFdceCount == 0) {
            String insFields = "_id, fdce_date, fdce_meal_no, fdce_energy, fdce_proteins, fdce_carbs, fdce_fat";
            String insValues = "NULL, " + stringDateSQL + ", " + stringMealNumberSQL + ", '0', '0', '0', '0'";
            db.insert("food_diary_cal_eaten", insFields, insValues);

            cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, whereClause, whereCondition, whereAndOr);
        }
        String stringFdceId = cursorFdce.getString(0);
        long longFdceId = Long.parseLong(stringFdceId);

        //Ready variables for sum
        int intFdceEnergy = 0;
        int intFdceProteins = 0;
        int intFdceCarbs = 0;
        int intFdceFat = 0;

        //Loop through cursor
        int intCursorFdCount = cursorFd.getCount();
        for (int x = 0; x < intCursorFdCount; x++) {

            //Variables from food diary
            String fdFoodId = cursorFd.getString(1);
            String fdFoodIdSQL = db.quoteSmart(fdFoodId);

            String fdServingSizeGram = cursorFd.getString(2);
            String fdServingSizeGramMesurment = cursorFd.getString(3);
            String fdServingSizePcs = cursorFd.getString(4);
            String fdServingSizePcsMesurment = cursorFd.getString(5);
            String fdEnergyCalculated = cursorFd.getString(6);
            String fdProteinsCalculated = cursorFd.getString(7);
            String fdCarbsCalculated = cursorFd.getString(8);
            String fdFatCalculated = cursorFd.getString(9);

            int intFdEnergyCalculated = Integer.parseInt(fdEnergyCalculated);
            int intFdProteinsCalculated = Integer.parseInt(fdProteinsCalculated);
            int intFdCarbsCalculated = Integer.parseInt(fdCarbsCalculated);
            int intFdFatCalculated = Integer.parseInt(fdFatCalculated);


            //Get food name
            cursorFood = db.select("food", fieldsFood, "_id", fdFoodIdSQL);

            //Variables from food
            String foodID = cursorFood.getString(0);
            String foodName = cursorFood.getString(1);
            String foodManufactorName = cursorFood.getString(2);

            String subLine = foodManufactorName + ", " +
                    fdServingSizeGram + ", " +
                    fdServingSizeGramMesurment + ", " +
                    fdServingSizePcs + ", " +
                    fdServingSizePcsMesurment;

            //Add table rows
            TableLayout t1 = null;
            if (stringMealNumber.equals("0")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutBreakfastItems);
            } else if (stringMealNumber.equals("1")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutLunchItems);
            } else if (stringMealNumber.equals("2")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutBeforeTrainingItems);
            } else if (stringMealNumber.equals("3")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutAfterTrainingItems);
            } else if (stringMealNumber.equals("4")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutDinnerItems);
            } else if (stringMealNumber.equals("5")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutSnacksItems);
            } else if (stringMealNumber.equals("6")) {
                t1 = (TableLayout) getActivity().findViewById(R.id.tableLayoutSupperItems);
            }

            TableRow tr1 = new TableRow(getActivity()); //Crete a new row to be added
            tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TableRow tr2 = new TableRow(getActivity()); //Crete a new row to be added
            tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            //Table row: TextView Name
            TextView textViewName = new TextView(getActivity());
            textViewName.setText(foodName);
            tr1.addView(textViewName);

            //Table row: TextView Energy
            TextView textViewEnergy = new TextView(getActivity());
            textViewEnergy.setText(fdEnergyCalculated);
            tr1.addView(textViewEnergy);

            //Table row: TextView subLine
            TextView textViewSubLine = new TextView(getActivity());
            textViewSubLine.setText(subLine);
            tr2.addView(textViewSubLine);

            //Add row to table
            t1.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            t1.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            //Add Listener
            tr1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                    //Get the row text
                    Context context = getContext();
                    TableRow row = (TableRow) v;
                    TextView tv = (TextView) row.getChildAt(0);

                    //Send it to edit
                    String tvText = "" + tv.getText();
                    rowOnClickDeleteFdLine(tvText);

                }
            });

            //Add Listener
            tr2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                    //Get the row text
                    Context context = getContext();
                    TableRow row = (TableRow) v;
                    TextView tv = (TextView) row.getChildAt(0);

                    //Send it to edit
                    String tvText = "" + tv.getText();
                    rowOnClickDeleteFdLine(tvText);

                }
            });

            //Sum fields
            intFdceEnergy = intFdceEnergy + intFdEnergyCalculated;
            intFdceProteins = intFdceProteins + intFdProteinsCalculated;
            intFdceCarbs = intFdceCarbs + intFdCarbsCalculated;
            intFdceFat = intFdceFat + intFdFatCalculated;

            cursorFd.moveToNext();

        }

        //Update fdce
        TextView textViewEnergyX = null;
        if (stringMealNumber.equals("0")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergyBreakfast);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("1")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergyLunch);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("2")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergyBeforeTraining);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("3")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergyAfterTraining);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("4")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergyDinner);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("5")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergySnacks);
            textViewEnergyX.setText("" + intFdceEnergy);
        } else if (stringMealNumber.equals("6")) {
            textViewEnergyX = (TextView) getActivity().findViewById(R.id.textViewEnergySupper);
            textViewEnergyX.setText("" + intFdceEnergy);
        }

        String updateFields[] = new String[]{
                "fdce_energy",
                "fdce_proteins",
                "fdce_carbs",
                "fdce_fat"
        };

        String updateValues[] = new String[]{
                "'" + intFdceEnergy + "'",
                "'" + intFdceProteins + "'",
                "'" + intFdceCarbs + "'",
                "'" + intFdceFat + "'"
        };

        db.update("food_diary_cal_eaten", "_id", longFdceId, updateFields, updateValues);

        db.close();

    }

    //Add food
    private void addFood(int mealNumber) {

        //Initialize fragment
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = AddFoodToDiaryFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Send variable
        Bundle bundle = new Bundle();
        bundle.putString("mealNumber", "" + mealNumber);
        fragment.setArguments(bundle);

        //Need to pass meal number
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    //Edit or delete fd line
    private void rowOnClickDeleteFdLine(String stringTableRowTextName) {

        //Initialize fragment
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = EditOrDeleteFoodToDiary.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Need to pass meal number
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    //calculateNumberOfCalEatenToday
    public void calculateNumberOfCalEatenToday(String stringDate) {

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Date SQL
        String stringDateSQL = db.quoteSmart(stringDate);

        //Food diary sum
        String fieldsFoodDiarySum[] = new String[]{
                "_id",
                "food_diary_sum_date",
                "food_diary_sum_energy",
                "food_diary_sum_proteins",
                "food_diary_sum_carbs",
                "food_diary_sum_fat"
        };

        Cursor cursorFoodDiarySum = db.select("food_diary_sum", fieldsFoodDiarySum, "food_diary_sum_date", stringDateSQL);
        int cursorFoodDiarySumCount = cursorFoodDiarySum.getCount();

        //Select for food_diary_cal_eaten
        String fieldsFdce[] = new String[]{
                "_id",
                "fdce_id",
                "fdce_meal_no",
                "fdce_energy",
                "fdce_proteins",
                "fdce_carbs",
                "fdce_fat"
        };

        //cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, "fdce_date", stringDateSQL);
        Cursor cursorFdce = db.select("food_diary_cal_eaten", fieldsFdce, "fdce_date", stringDateSQL);
        int cursorFdceCount = cursorFdce.getCount();

        //Ready variables
        int intFdceEatenEnergyTotal = 0;
        int intFdceEatenProteinsTotal = 0;
        int intFdceEatenCarbsTotal = 0;
        int intFdceEatenFatTotal = 0;

        String stringGetFdceMealNo = "";
        String stringGetFdceEatenEnergy = "";
        String stringGetFdceEatenProteins = "";
        String stringGetFdceEatenCarbs = "";
        String stringGetFdceEatenFat = "";
        int intFdceEatenEnergy = 0;
        int intFdceEatenProteins = 0;
        int intFdceEatenCarbs = 0;
        int intFdceEatenFat = 0;


        for (int x = 0; x < cursorFdceCount; x++) {

            //Get variables from cursor
            stringGetFdceMealNo = cursorFdce.getString(2);
            stringGetFdceEatenEnergy = cursorFdce.getString(3);
            stringGetFdceEatenProteins = cursorFdce.getString(4);
            stringGetFdceEatenCarbs = cursorFdce.getString(5);
            stringGetFdceEatenFat = cursorFdce.getString(6);

            try {
                intFdceEatenEnergy = Integer.parseInt(stringGetFdceEatenEnergy);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
            }

            try {
                intFdceEatenProteins = Integer.parseInt(stringGetFdceEatenProteins);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
            }

            try {
                intFdceEatenCarbs = Integer.parseInt(stringGetFdceEatenCarbs);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
            }

            try {
                intFdceEatenFat = Integer.parseInt(stringGetFdceEatenFat);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
            }

            intFdceEatenEnergyTotal = intFdceEatenEnergyTotal + intFdceEatenEnergy;
            intFdceEatenProteinsTotal = intFdceEatenProteinsTotal + intFdceEatenProteins;
            intFdceEatenCarbsTotal = intFdceEatenCarbsTotal + intFdceEatenCarbs;
            intFdceEatenFatTotal = intFdceEatenFatTotal + intFdceEatenFat;

            //Move to next
            cursorFdce.moveToNext();

        }

        if (cursorFoodDiarySumCount == 0) {

            //Insert database

            String insFields = "_id, food_diary_sum_date, food_diary_sum_energy, food_diary_sum_proteins, food_diary_sum_carbs, food_diary_sum_fat";
            String insValues = "NULL, " + stringDateSQL + ", '" + intFdceEatenEnergyTotal + "', '" +
                    intFdceEatenProteinsTotal + "', '" + intFdceEatenCarbsTotal + "', '" + intFdceEatenFatTotal + "'";
            db.insert("food_diary_sum", insFields, insValues);

        } else {

            //Update

            String updateFields[] = new String[] {
                    "food_diary_sum_energy",
                    "food_diary_sum_proteins",
                    "food_diary_sum_carbs",
                    "food_diary_sum_fat"
            };

            String updateValues[] = new String[] {
                    "'" + intFdceEatenEnergyTotal + "'",
                    "'" + intFdceEatenProteinsTotal + "'",
                    "'" + intFdceEatenCarbsTotal + "'",
                    "'" + intFdceEatenFatTotal + "'"
            };

            long longFoodDiaryId = Long.parseLong(cursorFoodDiarySum.getString(0));

            db.update("food_diary_sum", "_id", longFoodDiaryId, updateFields, updateValues);

        }

        //Get goal
        String fieldsGoal[] = new String[]{
                "_id",
                "goal_energy_with_activity_and_diet"
        };

        Cursor cursorGoal = db.select("goal", fieldsGoal);
        String stringGoalEnergyWithActivityAndDiet = "";
        if (cursorGoal.getCount() != 0) {
            cursorGoal.moveToLast();
            stringGoalEnergyWithActivityAndDiet = cursorGoal.getString(1);
        }

        //TextView goal
        TextView textViewBodyGoalWithActivity = (TextView) getActivity().findViewById(R.id.textViewBodyGoalWithActivity);
        textViewBodyGoalWithActivity.setText(stringGoalEnergyWithActivityAndDiet);

        //TextView food
        TextView textViewBodyFood = (TextView) getActivity().findViewById(R.id.textViewBodyFood);
        textViewBodyFood.setText("" + intFdceEatenEnergyTotal);

        //TextView result
        int intGoalEnergyWithActivityAndDiet = 0;
        try {
            intGoalEnergyWithActivityAndDiet = Integer.parseInt(stringGoalEnergyWithActivityAndDiet);
        } catch (NumberFormatException nfe) {
            System.out.println(nfe);
        }

        int textViewBodyResult = intGoalEnergyWithActivityAndDiet - intFdceEatenEnergyTotal;

        TextView textViewBodyRemaining = (TextView) getActivity().findViewById(R.id.textViewBodyRemaining);
        textViewBodyRemaining.setText("" + textViewBodyResult);


        db.close();

    }

}