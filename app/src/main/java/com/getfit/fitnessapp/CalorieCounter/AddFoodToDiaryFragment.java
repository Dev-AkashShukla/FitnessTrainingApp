package com.getfit.fitnessapp.CalorieCounter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getfit.fitnessapp.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFoodToDiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodToDiaryFragment extends Fragment {

    private View mainView;
    private Cursor listCursorCategory;
    private Cursor listCursorFood;

    //Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    //Holder
    private String currentMealNumber;
    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;

    private String currentPortionSizePcs;
    private String currentPortionSizeGram;
    private boolean lockPortionSizeByPcs;
    private boolean lockPortionSizeByGram;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFoodToDiaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFoodToDiaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFoodToDiaryFragment newInstance(String param1, String param2) {
        AddFoodToDiaryFragment fragment = new AddFoodToDiaryFragment();
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
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Add food to diary");

        //Get data from fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentMealNumber = bundle.getString("mealNumber");
        }

        //Populate the list of categories
        populateListWithCategories("0", "");

        //Create menu
        //setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_add_food_to_diary, container, false);
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
//        if (id == R.id.menu_action_goal_edit) {
//
//        }
        return super.onOptionsItemSelected(item);
    }

    //Our own methods

    //Populate list with categories
    public void populateListWithCategories(String stringCategoryParentID, String stringCategoryName) {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", stringCategoryParentID, "category_name", "ASC");

        //Create an array
        ArrayList<String> values = new ArrayList<String>();

        //Convert categories to string
        int categoriesCount = listCursorCategory.getCount();
        for (int x = 0; x < categoriesCount; x++) {
            values.add(listCursorCategory.getString(listCursorCategory.getColumnIndexOrThrow("category_name")));

            listCursorCategory.moveToNext();
        }

        //Close cursor
//        categoriesCursor.close();

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);

        //Set Adapter
        ListView listView = (ListView)getActivity().findViewById(R.id.listViewAddFoodToDiary);
        listView.setAdapter(adapter);

        //OnClick
        if (stringCategoryParentID.equals("0")) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    categoryListItemClicked(position);
                }
            });
        }

        db.close();

    }

    //Category list item clicked
    public void categoryListItemClicked(int listItemIndexClicked) {

        //Move cursor to ID clicked
        listCursorCategory.moveToPosition(listItemIndexClicked);

        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentCategoryID = listCursorCategory.getString(2);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Add food from " + currentCategoryName + " to diary");

        //Move to sub class
        populateListWithCategories(currentCategoryId, currentCategoryName);

        //Show food in category
        showFoodInCategory(currentCategoryId, currentCategoryName, parentCategoryID);

    }

    //Show food in category
    public void showFoodInCategory(String categoryId, String categoryName, String categoryParentID) {
        if (!(categoryParentID.equals("0"))) {
            //Change layout
            int id = R.layout.fragment_food_diet;
            setMainView(id);

            //Database
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            //Get categories
            String fields[] = new String[] {
                    "_id",
                    "food_name",
                    "food_manufactor_name",
                    "food_description",
                    "food_serving_size_gram",
                    "food_serving_size_gram_mesurment",
                    "food_serving_size_pcs",
                    "food_serving_size_pcs_mesurment",
                    "food_energy_calculated"
            };

            listCursorFood = db.select("food", fields, "food_category_id", categoryId, "food_name", "ASC");

            //Find listView to populate
            ListView listView = (ListView)getActivity().findViewById(R.id.listViewFood);

            //Setup cursor adapter using cursor from last step
            FoodCursorAdapter cursorAdapter = new FoodCursorAdapter(getActivity(), listCursorFood);

            listView.setAdapter(cursorAdapter);

            //OnClick

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    foodInCategoryListItemClicked(position);
                }
            });


            db.close();
        }
    }

    private void foodInCategoryListItemClicked(int listItemFoodIndexClicked) {

        //Change layout
        int id = R.layout.fragment_add_food_to_diary_view_food;
        setMainView(id);

        //Move cursor to ID clicked
        listCursorFood.moveToPosition(listItemFoodIndexClicked);

        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Add " + currentFoodName);

        //Get data from database

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        String fields[] = new String[] {
                "_id",
                " food_name",
                " food_manufactor_name",
                " food_description",
                " food_serving_size_gram",
                " food_serving_size_gram_mesurment",
                " food_serving_size_pcs",
                " food_serving_size_pcs_mesurment",
                " food_energy",
                " food_proteins",
                " food_carbohydrates",
                " food_fat",
                " food_energy_calculated",
                " food_proteins_calculated",
                " food_carbohydrates_calculated",
                " food_fat_calculated",
                " food_user_id",
                " food_barcode",
                " food_category_id",
                " food_image_a",
                " food_image_b",
                " food_image_c"
        };

        String currentIdSQL = db.quoteSmart(currentFoodId);

        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        //Convert cursor to strings
        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSize = foodCursor.getString(4);
        String stringServingMesurment = foodCursor.getString(5);
        String stringServingNameNumber = foodCursor.getString(6);
        String stringServingNameWord = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);
        String stringEnergyCalculated = foodCursor.getString(12);
        String stringProteinsCalculated = foodCursor.getString(13);
        String stringCarbohydratesCalculated = foodCursor.getString(14);
        String stringFatCalculated = foodCursor.getString(15);
        String stringUserId = foodCursor.getString(16);
        String stringBarcode = foodCursor.getString(17);
        String stringCategoryId = foodCursor.getString(18);
        String stringImageA = foodCursor.getString(19);
        String stringImageB = foodCursor.getString(20);
        String stringImageC = foodCursor.getString(21);

        //Update current
        currentPortionSizePcs = stringServingNameNumber;
        currentPortionSizeGram = stringServingSize;

        //Headline
        TextView textViewFoodName = (TextView) getView().findViewById(R.id.textViewFoodName);
        textViewFoodName.setText(stringName);

        //Sub Headline
        TextView textViewFoodManufactorName = (TextView) getView().findViewById(R.id.textViewFoodManufactorName);
        textViewFoodManufactorName.setText(stringManufactorName);

        //Portion size
        EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextPortionSizePcs);
        editTextPortionSizePcs.setText(stringServingNameNumber);

        TextView textViewPcs = (TextView) getActivity().findViewById(R.id.textViewPortionPcs);
        textViewPcs.setText(stringServingNameWord);

        //Portion gram
        EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
        editTextPortionSizeGram.setText(stringServingSize);

        //Image

        //Calculation line
        TextView textViewFoodAbout = (TextView) getView().findViewById(R.id.textViewFoodAbout);
        String foodAbout = stringServingSize + " " + stringServingMesurment + " = "
                + stringServingNameNumber + " " + stringServingNameWord + ".";
        textViewFoodAbout.setText(foodAbout);

        //Description
        TextView textViewFoodDescription = (TextView) getView().findViewById(R.id.textViewFoodDescription);
        textViewFoodDescription.setText(stringDescription);

        //Calories table
        TextView textViewFoodEnergyPerHundred = (TextView) getView().findViewById(R.id.textViewFoodEnergyPerHundred);
        TextView textViewFoodProteinsPerHundred = (TextView) getView().findViewById(R.id.textViewFoodProteinsPerHundred);
        TextView textViewFoodCarbsPerHundred = (TextView) getView().findViewById(R.id.textViewFoodCarbsPerHundred);
        TextView textViewFoodFatPerHundred = (TextView) getView().findViewById(R.id.textViewFoodFatPerHundred);

        TextView textViewFoodEnergyPerN = (TextView) getView().findViewById(R.id.textViewFoodEnergyPerMeal);
        TextView textViewFoodProteinsPerN = (TextView) getView().findViewById(R.id.textViewFoodProteinsPerMeal);
        TextView textViewFoodCarbsPerN = (TextView) getView().findViewById(R.id.textViewFoodCarbsPerMeal);
        TextView textViewFoodFatPerN = (TextView) getView().findViewById(R.id.textViewFoodFatPerMeal);

        textViewFoodEnergyPerHundred.setText(stringEnergy);
        textViewFoodProteinsPerHundred.setText(stringProteins);
        textViewFoodCarbsPerHundred.setText(stringCarbohydrates);
        textViewFoodFatPerHundred.setText(stringFat);

        textViewFoodEnergyPerN.setText(stringEnergyCalculated);
        textViewFoodProteinsPerN.setText(stringProteinsCalculated);
        textViewFoodCarbsPerN.setText(stringCarbohydratesCalculated);
        textViewFoodFatPerN.setText(stringFatCalculated);

        //Listener for editTextPortionSizePcs
        editTextPortionSizePcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().equals(""))) {
                    editTextPortionSizePcsOnChange();
                }
            }
        });
        editTextPortionSizePcs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    String lock = "portionSizePcs";
                    releaseLock(lock);
                }
            }
        });

        //Listener for editTextPortionSizeGram
        editTextPortionSizeGram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().equals(""))) {
                    editTextPortionSizeGramOnChange();
                }
            }
        });
        editTextPortionSizeGram.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    String lock = "portionSizeGram";
                    releaseLock(lock);
                }
            }
        });

        //Listener for add
        Button buttonAddToDiary = (Button) getActivity().findViewById(R.id.buttonAddToDiary);
        buttonAddToDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToDiary();
            }
        });

        db.close();

    }

    private void releaseLock(String lock) {
        if (lock.equals("portionSizeGram")) {
            lockPortionSizeByGram = false;
        } else {
            lockPortionSizeByPcs = false;
        }
    }

    //editTextPortionSizePcsOnChange
    public void editTextPortionSizePcsOnChange() {

        if (!(lockPortionSizeByGram)) {

            //Lock
            lockPortionSizeByPcs = true;

            //Get value of pcs
            EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextPortionSizePcs);
            String stringPortionSizePcs = editTextPortionSizePcs.getText().toString();

            double doublePortionSizePcs = 0;

            if (stringPortionSizePcs.equals("")) {
                doublePortionSizePcs = 0;
            } else {

                try {
                    doublePortionSizePcs = Double.parseDouble(stringPortionSizePcs);
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

            }

            //Get data from database

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[] {
                    " food_serving_size_gram"
            };

            String currentIdSQL = db.quoteSmart(currentFoodId);

            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            //Convert cursor to strings
            String stringServingSize = foodCursor.getString(0);

            db.close();

            //Convert cursor to double
            double doubleServingSize = 0;
            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            //Calculate how much n portion size in gram
            // We are changing pcs
            //Update gram
            double doublePortionSizeGram = Math.round(doublePortionSizePcs * doubleServingSize);

            //Update portion size gram
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
            editTextPortionSizeGram.setText("" + doublePortionSizeGram);


        }

    }

    //editTextPortionSizeGramOnChange
    public void editTextPortionSizeGramOnChange() {

        if (!(lockPortionSizeByPcs)) {

            lockPortionSizeByGram = true;

            //Get value of gram
            EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
            String stringPortionSizeGram = editTextPortionSizeGram.getText().toString();

            double doublePortionSizeGram = 0;

            if (stringPortionSizeGram.equals("")) {
                doublePortionSizeGram = 0;
            } else {

                try {
                    doublePortionSizeGram = Double.parseDouble(stringPortionSizeGram);
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

            }

            //Get data from database

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            String fields[] = new String[] {
                    " food_serving_size_gram"
            };

            String currentIdSQL = db.quoteSmart(currentFoodId);

            Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

            //Convert cursor to strings
            String stringServingSizeGram = foodCursor.getString(0);

            db.close();

            //Convert cursor to double
            double doubleServingSizeGram = 0;
            try {
                doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            //Calculate pcs
            double doublePortionSizePcs = Math.round(doublePortionSizeGram / doubleServingSizeGram);

            //Update portion size gram
            EditText editTextPortionSizePcs = (EditText) getActivity().findViewById(R.id.editTextPortionSizePcs);
            editTextPortionSizePcs.setText("" + doublePortionSizePcs);

        }

    }

    //Add food to diary
    public void addFoodToDiary() {

        //We want to add food

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();


        String fields[] = new String[] {
                "_id",
                " food_name",
                " food_manufactor_name",
                " food_description",
                " food_serving_size_gram",
                " food_serving_size_gram_mesurment",
                " food_serving_size_pcs",
                " food_serving_size_pcs_mesurment",
                " food_energy",
                " food_proteins",
                " food_carbohydrates",
                " food_fat",
                " food_energy_calculated",
                " food_proteins_calculated",
                " food_carbohydrates_calculated",
                " food_fat_calculated",
                " food_user_id",
                " food_barcode",
                " food_category_id",
                " food_image_a",
                " food_image_b",
                " food_image_c"
        };

        String currentIdSQL = db.quoteSmart(currentFoodId);

        Cursor foodCursor = db.select("food", fields, "_id", currentIdSQL);

        //Convert cursor to strings
        String stringId = foodCursor.getString(0);
        String stringName = foodCursor.getString(1);
        String stringManufactorName = foodCursor.getString(2);
        String stringDescription = foodCursor.getString(3);
        String stringServingSizeGram = foodCursor.getString(4);
        String stringServingSizeGramMesurment = foodCursor.getString(5);
        String stringServingSizePcs = foodCursor.getString(6);
        String stringServingSizePcsMesurment = foodCursor.getString(7);
        String stringEnergy = foodCursor.getString(8);
        String stringProteins = foodCursor.getString(9);
        String stringCarbohydrates = foodCursor.getString(10);
        String stringFat = foodCursor.getString(11);
        String stringEnergyCalculated = foodCursor.getString(12);
        String stringProteinsCalculated = foodCursor.getString(13);
        String stringCarbohydratesCalculated = foodCursor.getString(14);
        String stringFatCalculated = foodCursor.getString(15);
        String stringUserId = foodCursor.getString(16);
        String stringBarcode = foodCursor.getString(17);
        String stringCategoryId = foodCursor.getString(18);
        String stringImageA = foodCursor.getString(19);
        String stringImageB = foodCursor.getString(20);
        String stringImageC = foodCursor.getString(21);

        //Get gram
        EditText editTextPortionSizeGram = (EditText) getActivity().findViewById(R.id.editTextPortionSizeGram);
        String fdServingSizeGram = editTextPortionSizeGram.getText().toString();
        String fdServingSizeGramSQL = db.quoteSmart(fdServingSizeGram);
        double doublePortionSizeGram = 0;
        try {
            doublePortionSizeGram = Double.parseDouble(fdServingSizeGram);
        } catch (NumberFormatException nfe) {
            System.out.println(nfe);
        }

        //Date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Month
        month = month + 1; //Month starts with 0
        String stringMonth = "";
        if (month < 10) {
            stringMonth = "0" + month;
        } else {
            stringMonth = "" + month;
        }

        //Day
        String stringDay = "";
        if (day < 10) {
            stringDay = "0" + day;
        } else {
            stringDay = "" + day;
        }

        String stringFdDate = year + "-" + stringMonth + "-" + stringDay;
        String stringFdDateSQL = db.quoteSmart(stringFdDate);

        //Meal number
        String stringFdMealNumber = currentMealNumber;
        String stringFdMealNumberSQL = db.quoteSmart(stringFdMealNumber);

        //Food id
        String stringFdFoodId = currentFoodId;
        String stringFdFoodIdSQL = db.quoteSmart(stringFdFoodId);

        //Serving size
        String fdServingSizeGramMesurmentSQL = db.quoteSmart(stringServingSizeGramMesurment);

        //Serving size pcs
        double doubleServingSizeGram = 0;
        try {
            doubleServingSizeGram = Double.parseDouble(stringServingSizeGram);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        double doublePortionSizePcs = Math.round(doublePortionSizeGram / doubleServingSizeGram);
        String stringFdServingSizePcs = "" + doublePortionSizePcs;
        String stringFdServingSizePcsSQL = db.quoteSmart(stringFdServingSizePcs);

        // fd_serving_size_pcs_mesurment
        String stringFdServingSizePcsMesurmentSQL = db.quoteSmart(stringServingSizePcsMesurment);

        //Energy calculated
        double doubleEnergyPerHundred = Double.parseDouble(stringEnergy);

        double doubleFdEnergyCalculated = Math.round((doublePortionSizeGram * doubleEnergyPerHundred) / 100);
        String stringFdEnergyCalculated = "" + doubleFdEnergyCalculated;
        String stringFdEnergyCalculatedSQL = db.quoteSmart(stringFdEnergyCalculated);

        //Proteins calculated
        double doubleProteinsPerHundred = Double.parseDouble(stringProteins);

        double doubleFdProteinsCalculated = Math.round((doublePortionSizeGram * doubleProteinsPerHundred) / 100);
        String stringFdProteinsCalculated = "" + doubleFdProteinsCalculated;
        String stringFdProteinsCalculatedSQL = db.quoteSmart(stringFdProteinsCalculated);

        //Carbohydrates calculated
        double doubleCarbohydratesPerHundred = Double.parseDouble(stringCarbohydrates);

        double doubleFdCarbohydratesCalculated = Math.round((doublePortionSizeGram * doubleCarbohydratesPerHundred) / 100);
        String stringFdCarbohydratesCalculated = "" + doubleFdCarbohydratesCalculated;
        String stringFdCarbohydratesCalculatedSQL = db.quoteSmart(stringFdCarbohydratesCalculated);

        //Fat calculated
        double doubleFatPerHundred = Double.parseDouble(stringFat);

        double doubleFdFatCalculated = Math.round((doublePortionSizeGram * doubleFatPerHundred) / 100);
        String stringFdFatCalculated = "" + doubleFdFatCalculated;
        String stringFdFatCalculatedSQL = db.quoteSmart(stringFdFatCalculated);

        if (TextUtils.isEmpty(fdServingSizeGram)) {
            Toast.makeText(getActivity(), "Grams is needed", Toast.LENGTH_SHORT).show();
            editTextPortionSizeGram.setError("Please fill in grams");
            editTextPortionSizeGram.requestFocus();
        } else {

            String inpFields = "_id, fd_date, fd_meal_number, fd_food_id," +
                    "fd_serving_size_gram, fd_serving_size_gram_mesurment," +
                    "fd_serving_size_pcs, fd_serving_size_pcs_mesurment," +
                    "fd_energy_calculated, fd_protein_calculated," +
                    "fd_carbohydrates_calculated, fd_fat_calculated";

            String inpValues = "NULL, " + stringFdDateSQL + ", " + stringFdMealNumberSQL + ", " + stringFdFoodIdSQL + ", " +
                    fdServingSizeGramSQL + ", " + fdServingSizeGramMesurmentSQL + ", " +
                    stringFdServingSizePcsSQL + ", " + stringFdServingSizePcsMesurmentSQL + ", " +
                    stringFdEnergyCalculatedSQL + ", " + stringFdProteinsCalculatedSQL + ", " +
                    stringFdCarbohydratesCalculatedSQL + ", " + stringFdFatCalculatedSQL;

            db.insert("food_diary", inpFields, inpValues);

            Toast.makeText(getActivity(), "Food diary updated", Toast.LENGTH_SHORT).show();

            //Change fragment to HomeFragment
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeDietFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        }

        //Close db
        db.close();

    }

}