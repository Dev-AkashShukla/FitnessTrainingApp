package com.getfit.fitnessapp.CalorieCounter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getfit.fitnessapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodDietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodDietFragment extends Fragment {

    private Cursor listCursor;
    private View mainView;

    //Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    //Holder for buttons on toolbar
    private String currentId = "";
    private String currentName;

    private String mParam1;
    private String mParam2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    public FoodDietFragment() {
        // Required empty public constructor
    }

    public static FoodDietFragment newInstance(String param1, String param2) {
        FoodDietFragment fragment = new FoodDietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Set main view
    private void setMainView(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    //03 on Activity Created
    //Run methods when started
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Food");

        populateListFood();

//        //Get data from fragment
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            currentId = bundle.getString("currentFoodId");
//        }
//        if (currentId.equals("")) {
//            //Populate the list of food
//            populateListFood();
//        } else {
//            preListItemClickedReadyCursor(); // We are coming from another class with currentFoodId
//        }

        //Create menu
        setHasOptionsMenu(true);
    }

    //04 On create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_food_diet, container, false);
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ((DietActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_food, menu);

        // Assign menu items to variables
        menuItemEdit = menu.findItem(R.id.menu_action_food_edit);
        menuItemDelete = menu.findItem(R.id.menu_action_food_delete);

        //Hide as default
        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);

    }

    //05 On Options Item Selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_action_food_add) {
            addFood();
        }
        if (id == R.id.menu_action_food_edit) {
            editFood();
        }
        if (id == R.id.menu_action_food_delete) {
            deleteFood();
        }

        return super.onOptionsItemSelected(item);
    }

    //06 Our own methods

    //Populate List
    public void populateListFood() {

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
        listCursor = db.select("food", fields, "", "", "food_name", "ASC");

        //Find listView to populate
        ListView listView = (ListView)getActivity().findViewById(R.id.listViewFood);

        //Setup cursor adapter using cursor from last step
        FoodCursorAdapter cursorAdapter = new FoodCursorAdapter(getActivity(), listCursor);

        listView.setAdapter(cursorAdapter);

        //OnClick

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClicked(position);
            }
        });


        db.close();

    }

    //We are coming another class, and need the cursor
    public void preListItemClickedReadyCursor() {

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

        String currentIdSQL = db.quoteSmart(currentId);

        listCursor = db.select("food", fields, "_id", currentIdSQL, "food_name", "ASC");

        db.close();

    }

    //List item clicked
    public void listItemClicked(int position) {

        //Change layout
        int id = R.layout.fragment_food_view;
        setMainView(id);

        //Show edit button
        menuItemEdit.setVisible(true);
        menuItemDelete.setVisible(true);

        //Move cursor to ID clicked
        listCursor.moveToPosition(position);

        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle(currentName);

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

        String currentIdSQL = db.quoteSmart(currentId);

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


        //Headline
        TextView textViewFoodName = (TextView) getView().findViewById(R.id.textViewFoodName);
        textViewFoodName.setText(stringName);

        //Sub Headline
        TextView textViewFoodManufactorName = (TextView) getView().findViewById(R.id.textViewFoodManufactorName);
        textViewFoodManufactorName.setText(stringManufactorName);

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

        db.close();

    }

    //Edit food
    String selectedMainCategoryName = "";
    public void editFood() {

        //Change layout
        int id = R.layout.fragment_food_edit;
        setMainView(id);

        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Edit " + currentName);

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

        String currentIdSQL = db.quoteSmart(currentId);

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

        //General

        //Name
        EditText editTextFoodName = (EditText) getView().findViewById(R.id.editTextFoodEditName);
        editTextFoodName.setText(stringName);

        //Manufactor
        EditText editTextFoodManufactor = (EditText) getView().findViewById(R.id.editTextFoodEditManufactor);
        editTextFoodManufactor.setText(stringManufactorName);

        //Description
        EditText editTextFoodDescription = (EditText) getView().findViewById(R.id.editTextFoodEditDescription);
        editTextFoodDescription.setText(stringDescription);

        //Barcode
        EditText editTextFoodBarcode = (EditText) getView().findViewById(R.id.editTextFoodEditBarcode);
        editTextFoodBarcode.setText(stringBarcode);

        //What category food is in, and its parent
        String spinnerFields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };

        //Find the category that the food is using (has to be a sub category)
        Cursor dbCursorCurrentFoodCategory = db.select("categories", spinnerFields, "_id", stringCategoryId, "category_name", "ASC");

        String currentFoodCategoryID = dbCursorCurrentFoodCategory.getString(2);

        //Sub category
        Cursor dbCursorSub = db.select("categories", spinnerFields, "category_parent_id", currentFoodCategoryID, "category_name", "ASC");

        //Creating array
        int dbCursorCount = dbCursorSub.getCount();
        String[] arraySpinnerCategoriesSub = new String[dbCursorCount];

        //find out sub category selected
        int selectedSubCategoryIndex = 0;
        String selectedSubCategoryParentID = "0";

        //Convert Cursor to String
        for (int x = 0; x < dbCursorCount; x++) {
            arraySpinnerCategoriesSub[x] = dbCursorSub.getString(1).toString();

            if (dbCursorSub.getString(0).toString().equals(stringCategoryId)) {
                selectedSubCategoryIndex = x;
                selectedSubCategoryParentID = dbCursorSub.getString(2).toString();
            }

            dbCursorSub.moveToNext();
        }

        //Populate spinner
        Spinner spinnerCatSub = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategorySub);
        ArrayAdapter<String> adapterSub = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategoriesSub);
        spinnerCatSub.setAdapter(adapterSub);

        //Select index of sub
        spinnerCatSub.setSelection(selectedSubCategoryIndex);

        //Main category
        Cursor dbCursorMain = db.select("categories", spinnerFields, "category_parent_id", "0", "category_name", "ASC");

        //Creating array
        dbCursorCount = dbCursorMain.getCount();
        String[] arraySpinnerMainCategories = new String[dbCursorCount];

        //Select correct main category
        int selectedMainCategoryIndex = 0;

        //Convert Cursor to String
        for (int x = 0; x < dbCursorCount; x++) {
            arraySpinnerMainCategories[x] = dbCursorMain.getString(1).toString();

            if (dbCursorMain.getString(0).toString().equals(selectedSubCategoryParentID)) {
                selectedMainCategoryIndex = x;
                selectedMainCategoryName = dbCursorMain.getString(1).toString();
            }

            dbCursorMain.moveToNext();
        }

        //Populate spinner
        Spinner spinnerCatMain = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategoryMain);
        ArrayAdapter<String> adapterMain = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerMainCategories);
        spinnerCatMain.setAdapter(adapterMain);

        //Select index of sub
        spinnerCatMain.setSelection(selectedMainCategoryIndex);


        //Serving Table
        //Size
        EditText editTextServingSize = (EditText) getView().findViewById(R.id.editTextFoodEditServingSize);
        editTextServingSize.setText(stringServingSize);

        //Mesurment
        EditText editTextServingMesurment = (EditText) getView().findViewById(R.id.editTextFoodEditMesurment);
        editTextServingMesurment.setText(stringServingMesurment);

        //Serving Number
        EditText editTextServingNameNumber = (EditText) getView().findViewById(R.id.editTextFoodEditServingNumber);
        editTextServingNameNumber.setText(stringServingNameNumber);

        //Serving Word
        EditText editTextServingNameWord = (EditText) getView().findViewById(R.id.editTextFoodEditServingWord);
        editTextServingNameWord.setText(stringServingNameWord);


        //Calories table
        //Energy
        EditText editTextEnergyPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditEnergyPerHundred);
        editTextEnergyPerHundred.setText(stringEnergy);

        //Proteins
        EditText editTextProteinsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditProteinsPerHundred);
        editTextProteinsPerHundred.setText(stringProteins);

        //Carbs
        EditText editTextCarbsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditCarbsPerHundred);
        editTextCarbsPerHundred.setText(stringCarbohydrates);

        //Fat
        EditText editTextFatPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditFatPerHundred);
        editTextFatPerHundred.setText(stringFat);

        //Submit button listener
        Button buttonEditFood = (Button) getActivity().findViewById(R.id.buttonSaveFoodEdit);
        buttonEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEditFoodOnClick();
            }
        });

        //Listener for Main Category Change
        spinnerCatMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                editFoodMainCategoryChanged(selectedItem);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Close db
        db.close();

    }

    public void editFoodMainCategoryChanged(String selectedItemCategoryName) {
        if (!(selectedItemCategoryName.equals(selectedMainCategoryName))) {

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            //Find ID of main category
            String selectedItemCategoryNameSQL = db.quoteSmart(selectedItemCategoryName);
            String spinnerFields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };

            Cursor findMainCategoryID = db.select("categories", spinnerFields, "category_name",
                    selectedItemCategoryNameSQL);
            String stringMainCategoryID = findMainCategoryID.getString(0).toString();
            String stringMainCategoryIDSQL = db.quoteSmart(stringMainCategoryID);

            //Sub category
            Cursor dbCursorSub = db.select("categories", spinnerFields, "category_parent_id", stringMainCategoryIDSQL, "category_name", "ASC");

            //Creating array
            int dbCursorCount = dbCursorSub.getCount();
            String[] arraySpinnerCategoriesSub = new String[dbCursorCount];

            //Convert Cursor to String
            for (int x = 0; x < dbCursorCount; x++) {
                arraySpinnerCategoriesSub[x] = dbCursorSub.getString(1).toString();
                dbCursorSub.moveToNext();
            }

            //Populate spinner
            Spinner spinnerCatSub = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategorySub);
            ArrayAdapter<String> adapterSub = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinnerCategoriesSub);
            spinnerCatSub.setAdapter(adapterSub);

            db.close();

        }
    }

    //Edit food submit on click
    private void buttonEditFoodOnClick() {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //General

        //Name
        EditText editTextFoodName = (EditText) getView().findViewById(R.id.editTextFoodEditName);
        String stringName = editTextFoodName.getText().toString();

        //Manufactor
        EditText editTextFoodManufactor = (EditText) getView().findViewById(R.id.editTextFoodEditManufactor);
        String stringManufactorName = editTextFoodManufactor.getText().toString();

        //Description
        EditText editTextFoodDescription = (EditText) getView().findViewById(R.id.editTextFoodEditDescription);
        String stringDescription = editTextFoodDescription.getText().toString();

        //Barcode
        EditText editTextFoodBarcode = (EditText) getView().findViewById(R.id.editTextFoodEditBarcode);
        String stringBarcode = editTextFoodBarcode.getText().toString();

        //Serving Table
        //Size
        EditText editTextServingSize = (EditText) getView().findViewById(R.id.editTextFoodEditServingSize);
        String stringServingSize = editTextServingSize.getText().toString();

        //Mesurment
        EditText editTextServingMesurment = (EditText) getView().findViewById(R.id.editTextFoodEditMesurment);
        String stringServingMesurment = editTextServingMesurment.getText().toString();

        //Serving Number
        EditText editTextServingNameNumber = (EditText) getView().findViewById(R.id.editTextFoodEditServingNumber);
        String stringServingNameNumber = editTextServingNameNumber.getText().toString();

        //Serving Word
        EditText editTextServingNameWord = (EditText) getView().findViewById(R.id.editTextFoodEditServingWord);
        String stringServingNameWord = editTextServingNameWord.getText().toString();

        //Calories table
        //Energy
        EditText editTextEnergyPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditEnergyPerHundred);
        String stringEnergy = editTextEnergyPerHundred.getText().toString();

        //Proteins
        EditText editTextProteinsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditProteinsPerHundred);
        String stringProteins = editTextProteinsPerHundred.getText().toString();

        //Carbs
        EditText editTextCarbsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditCarbsPerHundred);
        String stringCarbohydrates = editTextCarbsPerHundred.getText().toString();

        //Fat
        EditText editTextFatPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditFatPerHundred);
        String stringFat = editTextFatPerHundred.getText().toString();

        double doubleEnergyPerHundred = 0;
        double doubleProteinsPerHundred = 0;
        double doubleCarbsPerHundred = 0;
        double doubleFatPerHundred = 0;
        double doubleServingSize = 0;
        double doubleNameNumber = 0;

        if (TextUtils.isEmpty(stringName)) {
            Toast.makeText(getActivity(), "Food name is needed", Toast.LENGTH_SHORT).show();
            editTextFoodName.setError("Please fill in a name");
            editTextFoodName.requestFocus();
        } else if (TextUtils.isEmpty(stringManufactorName)) {
            Toast.makeText(getActivity(), "Manufactor name is needed", Toast.LENGTH_SHORT).show();
            editTextFoodManufactor.setError("Please fill in a manufactor name");
            editTextFoodManufactor.requestFocus();
        } else if (TextUtils.isEmpty(stringDescription)) {
            Toast.makeText(getActivity(), "Description is needed", Toast.LENGTH_SHORT).show();
            editTextFoodDescription.setError("Please fill in a description");
            editTextFoodDescription.requestFocus();
        } else if (TextUtils.isEmpty(stringBarcode)) {
            Toast.makeText(getActivity(), "Barcode is needed", Toast.LENGTH_SHORT).show();
            editTextFoodBarcode.setError("Please fill in a barcode");
            editTextFoodBarcode.requestFocus();
        } else if (TextUtils.isEmpty(stringServingSize)) {
            Toast.makeText(getActivity(), "Serving size is needed", Toast.LENGTH_SHORT).show();
            editTextServingSize.setError("Please fill in a serving size");
            editTextServingSize.requestFocus();
        } else if (TextUtils.isEmpty(stringServingMesurment)) {
            Toast.makeText(getActivity(), "Serving mesurment is needed", Toast.LENGTH_SHORT).show();
            editTextServingMesurment.setError("Please fill in a serving mesurment");
            editTextServingMesurment.requestFocus();
        } else if (TextUtils.isEmpty(stringServingNameNumber)) {
            Toast.makeText(getActivity(), "Name number is needed", Toast.LENGTH_SHORT).show();
            editTextServingNameNumber.setError("Please fill in a name number");
            editTextServingNameNumber.requestFocus();
        } else if (TextUtils.isEmpty(stringServingNameWord)) {
            Toast.makeText(getActivity(), "Name word is needed", Toast.LENGTH_SHORT).show();
            editTextServingNameWord.setError("Please fill in a name word");
            editTextServingNameWord.requestFocus();
        } else if (TextUtils.isEmpty(stringEnergy)) {
            Toast.makeText(getActivity(), "Energy is needed", Toast.LENGTH_SHORT).show();
            editTextEnergyPerHundred.setError("Please fill in an energy");
            editTextEnergyPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringProteins)) {
            Toast.makeText(getActivity(), "Proteins is needed", Toast.LENGTH_SHORT).show();
            editTextProteinsPerHundred.setError("Please fill in proteins");
            editTextProteinsPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringCarbohydrates)) {
            Toast.makeText(getActivity(), "Carbs is needed", Toast.LENGTH_SHORT).show();
            editTextCarbsPerHundred.setError("Please fill in carbs");
            editTextCarbsPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringFat)) {
            Toast.makeText(getActivity(), "Fat is needed", Toast.LENGTH_SHORT).show();
            editTextFatPerHundred.setError("Please fill in a fat");
            editTextFatPerHundred.requestFocus();
        } else {
            try {
                doubleEnergyPerHundred = Double.parseDouble(stringEnergy);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Energy is not a number", Toast.LENGTH_SHORT).show();
                editTextEnergyPerHundred.setError("Please fill in an energy");
                editTextEnergyPerHundred.requestFocus();
            }

            try {
                doubleProteinsPerHundred = Double.parseDouble(stringProteins);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Proteins is not a number", Toast.LENGTH_SHORT).show();
                editTextProteinsPerHundred.setError("Please fill in proteins");
                editTextProteinsPerHundred.requestFocus();
            }

            try {
                doubleCarbsPerHundred = Double.parseDouble(stringCarbohydrates);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number", Toast.LENGTH_SHORT).show();
                editTextCarbsPerHundred.setError("Please fill in proteins");
                editTextCarbsPerHundred.requestFocus();
            }

            try {
                doubleFatPerHundred = Double.parseDouble(stringFat);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Fat is not a number", Toast.LENGTH_SHORT).show();
                editTextFatPerHundred.setError("Please fill in a fat");
                editTextFatPerHundred.requestFocus();
            }

            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Serving size is not a number", Toast.LENGTH_SHORT).show();
                editTextServingSize.setError("Please fill in a serving size");
                editTextServingSize.requestFocus();
            }

            try {
                doubleNameNumber = Double.parseDouble(stringServingNameNumber);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Name number is not a number", Toast.LENGTH_SHORT).show();
                editTextServingNameNumber.setError("Please fill in a name number");
                editTextServingNameNumber.requestFocus();
            }

            long rowID = Long.parseLong(currentId);

            //Category
            //Sub category

            Spinner spinnerSubCat = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategorySub);
            int intSpinnerSubCategory = spinnerSubCat.getSelectedItemPosition();

            String stringSpinnerSubCategoryName = spinnerSubCat.getSelectedItem().toString();

            //Find we want to find parent ID from the text
            String stringSpinnerSubCategoryNameSQL = db.quoteSmart(stringSpinnerSubCategoryName);

            String spinnerFields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };

            Cursor findStringSpinnerSubCategoryID = db.select("categories", spinnerFields, "category_name",
                    stringSpinnerSubCategoryNameSQL);

            String stringSubCategoryID = findStringSpinnerSubCategoryID.getString(0).toString();
            String stringSubCategoryIDSQL = db.quoteSmart(stringSubCategoryID);

            String stringNameSQL = db.quoteSmart(stringName);
            String stringManufactorNameSQL = db.quoteSmart(stringManufactorName);
            String stringDescriptionSQL = db.quoteSmart(stringDescription);
            String stringBarcodeSQL = db.quoteSmart(stringBarcode);

            String stringServingSizeSQL = db.quoteSmart(stringServingSize);
            String stringServingMesurmentSQL = db.quoteSmart(stringServingMesurment);
            String stringServingNameNumberSQL = db.quoteSmart(stringServingNameNumber);
            String stringServingNameWordSQL = db.quoteSmart(stringServingNameWord);

            double doubleEnergyPerHundredSQL = db.quoteSmart(doubleEnergyPerHundred);
            double doubleProteinsPerHundredSQL = db.quoteSmart(doubleProteinsPerHundred);
            double doubleCarbsPerHundredSQL = db.quoteSmart(doubleCarbsPerHundred);
            double doubleFatPerHundredSQL = db.quoteSmart(doubleFatPerHundred);

            String stringEnergyPerHundredSQL = db.quoteSmart(stringEnergy);
            String stringProteinsPerHundredSQL = db.quoteSmart(stringProteins);
            String stringCarbsPerHundredSQL = db.quoteSmart(stringCarbohydrates);
            String stringFatPerHundredSQL = db.quoteSmart(stringFat);

            double energyCalculated = (doubleEnergyPerHundred * doubleServingSize) / 100;
            double proteinsCalculated = (doubleProteinsPerHundred * doubleServingSize) / 100;
            double carbsCalculated = (doubleCarbsPerHundred * doubleServingSize) / 100;
            double fatCalculated = (doubleFatPerHundred * doubleServingSize) / 100;

            String stringEnergyCalculated = "" + energyCalculated;
            String stringProteinsCalculated = "" + proteinsCalculated;
            String stringCarbsCalculated = "" + carbsCalculated;
            String stringFatCalculated = "" + fatCalculated;

            String stringEnergyCalculatedSQL = db.quoteSmart(stringEnergyCalculated);
            String stringProteinsCalculatedSQL = db.quoteSmart(stringProteinsCalculated);
            String stringCarbsCalculatedSQL = db.quoteSmart(stringCarbsCalculated);
            String stringFatCalculatedSQL = db.quoteSmart(stringFatCalculated);

            String fields[] = new String[] {
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
                    " food_barcode",
                    " food_category_id"
            };

            String values[] = new String[] {
                    stringNameSQL,
                    stringManufactorNameSQL,
                    stringDescriptionSQL,
                    stringServingSizeSQL,
                    stringServingMesurmentSQL,
                    stringServingNameNumberSQL,
                    stringServingNameWordSQL,
                    stringEnergyPerHundredSQL,
                    stringProteinsPerHundredSQL,
                    stringCarbsPerHundredSQL,
                    stringFatPerHundredSQL,
                    stringEnergyCalculatedSQL,
                    stringProteinsCalculatedSQL,
                    stringCarbsCalculatedSQL,
                    stringFatCalculatedSQL,
                    stringBarcodeSQL,
                    stringSubCategoryIDSQL
            };

            db.update("food", "_id", rowID, fields, values);
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new FoodDietFragment(), FoodDietFragment.class.getName()).commit();
        }
        db.close();
    }
    
    //Delete food
    public void deleteFood() {
        
        //Change layout
        int id = R.layout.fragment_food_delete;
        setMainView(id);

        Button buttonCancel = (Button) getActivity().findViewById(R.id.button_food_delete_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFoodCancelOnClick();
            }
        });

        Button buttonConfirmDelete = (Button) getActivity().findViewById(R.id.button_food_delete);
        buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFoodConfirmOnClick();
            }
        });
    }

    private void deleteFoodConfirmOnClick() {
        //Delete from SQL

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Current ID to long
        long longCurrentID = Long.parseLong(currentId);

        //Ready variables
        long currentIDSQL = db.quoteSmart(longCurrentID);

        //Delete
        db.delete("food", "_id", currentIDSQL);

        //Close db
        db.close();

        Toast.makeText(getActivity(), "Food deleted", Toast.LENGTH_LONG).show();

        //Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new FoodDietFragment(), FoodDietFragment.class.getName()).commit();

    }

    private void deleteFoodCancelOnClick() {
        //Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new FoodDietFragment(), FoodDietFragment.class.getName()).commit();
    }

    public void addFood() {
        //Change layout
        int id = R.layout.fragment_food_edit;
        setMainView(id);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Add food");

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Main category
        String spinnerFields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };

        Cursor dbCursorMain = db.select("categories", spinnerFields, "category_parent_id", "0", "category_name", "ASC");

        //Creating array
        int dbCursorCount = dbCursorMain.getCount();
        String[] arraySpinnerMainCategories = new String[dbCursorCount];

        //Convert Cursor to String
        for (int x = 0; x < dbCursorCount; x++) {
            arraySpinnerMainCategories[x] = dbCursorMain.getString(1).toString();
            dbCursorMain.moveToNext();
        }

        //Populate spinner
        Spinner spinnerCatMain = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategoryMain);
        ArrayAdapter<String> adapterMain = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerMainCategories);
        spinnerCatMain.setAdapter(adapterMain);

        //Listener for Main Category Change
        spinnerCatMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                editFoodMainCategoryChanged(selectedItem);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Submit button listener
        Button buttonEditFood = (Button) getActivity().findViewById(R.id.buttonSaveFoodEdit);
        buttonEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddFoodOnClick();
            }
        });

    }

    public void buttonAddFoodOnClick() {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //General

        //Name
        EditText editTextFoodName = (EditText) getView().findViewById(R.id.editTextFoodEditName);
        String stringName = editTextFoodName.getText().toString();

        //Manufactor
        EditText editTextFoodManufactor = (EditText) getView().findViewById(R.id.editTextFoodEditManufactor);
        String stringManufactorName = editTextFoodManufactor.getText().toString();

        //Description
        EditText editTextFoodDescription = (EditText) getView().findViewById(R.id.editTextFoodEditDescription);
        String stringDescription = editTextFoodDescription.getText().toString();

        //Barcode
        EditText editTextFoodBarcode = (EditText) getView().findViewById(R.id.editTextFoodEditBarcode);
        String stringBarcode = editTextFoodBarcode.getText().toString();

        //Serving Table
        //Size
        EditText editTextServingSize = (EditText) getView().findViewById(R.id.editTextFoodEditServingSize);
        String stringServingSize = editTextServingSize.getText().toString();

        //Mesurment
        EditText editTextServingMesurment = (EditText) getView().findViewById(R.id.editTextFoodEditMesurment);
        String stringServingMesurment = editTextServingMesurment.getText().toString();

        //Serving Number
        EditText editTextServingNameNumber = (EditText) getView().findViewById(R.id.editTextFoodEditServingNumber);
        String stringServingNameNumber = editTextServingNameNumber.getText().toString();

        //Serving Word
        EditText editTextServingNameWord = (EditText) getView().findViewById(R.id.editTextFoodEditServingWord);
        String stringServingNameWord = editTextServingNameWord.getText().toString();

        //Calories table
        //Energy
        EditText editTextEnergyPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditEnergyPerHundred);
        String stringEnergy = editTextEnergyPerHundred.getText().toString();

        //Proteins
        EditText editTextProteinsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditProteinsPerHundred);
        String stringProteins = editTextProteinsPerHundred.getText().toString();

        //Carbs
        EditText editTextCarbsPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditCarbsPerHundred);
        String stringCarbohydrates = editTextCarbsPerHundred.getText().toString();

        //Fat
        EditText editTextFatPerHundred = (EditText) getView().findViewById(R.id.editTextFoodEditFatPerHundred);
        String stringFat = editTextFatPerHundred.getText().toString();

        double doubleEnergyPerHundred = 0;
        double doubleProteinsPerHundred = 0;
        double doubleCarbsPerHundred = 0;
        double doubleFatPerHundred = 0;
        double doubleServingSize = 0;
        double doubleNameNumber = 0;

        if (TextUtils.isEmpty(stringName)) {
            Toast.makeText(getActivity(), "Food name is needed", Toast.LENGTH_SHORT).show();
            editTextFoodName.setError("Please fill in a name");
            editTextFoodName.requestFocus();
        } else if (TextUtils.isEmpty(stringManufactorName)) {
            Toast.makeText(getActivity(), "Manufactor name is needed", Toast.LENGTH_SHORT).show();
            editTextFoodManufactor.setError("Please fill in a manufactor name");
            editTextFoodManufactor.requestFocus();
        } else if (TextUtils.isEmpty(stringDescription)) {
            Toast.makeText(getActivity(), "Description is needed", Toast.LENGTH_SHORT).show();
            editTextFoodDescription.setError("Please fill in a description");
            editTextFoodDescription.requestFocus();
        } else if (TextUtils.isEmpty(stringBarcode)) {
            Toast.makeText(getActivity(), "Barcode is needed", Toast.LENGTH_SHORT).show();
            editTextFoodBarcode.setError("Please fill in a barcode");
            editTextFoodBarcode.requestFocus();
        } else if (TextUtils.isEmpty(stringServingSize)) {
            Toast.makeText(getActivity(), "Serving size is needed", Toast.LENGTH_SHORT).show();
            editTextServingSize.setError("Please fill in a serving size");
            editTextServingSize.requestFocus();
        } else if (TextUtils.isEmpty(stringServingMesurment)) {
            Toast.makeText(getActivity(), "Serving mesurment is needed", Toast.LENGTH_SHORT).show();
            editTextServingMesurment.setError("Please fill in a serving mesurment");
            editTextServingMesurment.requestFocus();
        } else if (TextUtils.isEmpty(stringServingNameNumber)) {
            Toast.makeText(getActivity(), "Name number is needed", Toast.LENGTH_SHORT).show();
            editTextServingNameNumber.setError("Please fill in a name number");
            editTextServingNameNumber.requestFocus();
        } else if (TextUtils.isEmpty(stringServingNameWord)) {
            Toast.makeText(getActivity(), "Name word is needed", Toast.LENGTH_SHORT).show();
            editTextServingNameWord.setError("Please fill in a name word");
            editTextServingNameWord.requestFocus();
        } else if (TextUtils.isEmpty(stringEnergy)) {
            Toast.makeText(getActivity(), "Energy is needed", Toast.LENGTH_SHORT).show();
            editTextEnergyPerHundred.setError("Please fill in an energy");
            editTextEnergyPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringProteins)) {
            Toast.makeText(getActivity(), "Proteins is needed", Toast.LENGTH_SHORT).show();
            editTextProteinsPerHundred.setError("Please fill in proteins");
            editTextProteinsPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringCarbohydrates)) {
            Toast.makeText(getActivity(), "Carbs is needed", Toast.LENGTH_SHORT).show();
            editTextCarbsPerHundred.setError("Please fill in carbs");
            editTextCarbsPerHundred.requestFocus();
        } else if (TextUtils.isEmpty(stringFat)) {
            Toast.makeText(getActivity(), "Fat is needed", Toast.LENGTH_SHORT).show();
            editTextFatPerHundred.setError("Please fill in a fat");
            editTextFatPerHundred.requestFocus();
        } else {
            try {
                doubleEnergyPerHundred = Double.parseDouble(stringEnergy);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Energy is not a number", Toast.LENGTH_SHORT).show();
                editTextEnergyPerHundred.setError("Please fill in an energy");
                editTextEnergyPerHundred.requestFocus();
            }

            try {
                doubleProteinsPerHundred = Double.parseDouble(stringProteins);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Proteins is not a number", Toast.LENGTH_SHORT).show();
                editTextProteinsPerHundred.setError("Please fill in proteins");
                editTextProteinsPerHundred.requestFocus();
            }

            try {
                doubleCarbsPerHundred = Double.parseDouble(stringCarbohydrates);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Carbs is not a number", Toast.LENGTH_SHORT).show();
                editTextCarbsPerHundred.setError("Please fill in proteins");
                editTextCarbsPerHundred.requestFocus();
            }

            try {
                doubleFatPerHundred = Double.parseDouble(stringFat);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Fat is not a number", Toast.LENGTH_SHORT).show();
                editTextFatPerHundred.setError("Please fill in a fat");
                editTextFatPerHundred.requestFocus();
            }

            try {
                doubleServingSize = Double.parseDouble(stringServingSize);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Serving size is not a number", Toast.LENGTH_SHORT).show();
                editTextServingSize.setError("Please fill in a serving size");
                editTextServingSize.requestFocus();
            }

            try {
                doubleNameNumber = Double.parseDouble(stringServingNameNumber);
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), "Name number is not a number", Toast.LENGTH_SHORT).show();
                editTextServingNameNumber.setError("Please fill in a name number");
                editTextServingNameNumber.requestFocus();
            }
            //Category
            //Sub category

            Spinner spinnerSubCat = (Spinner) getActivity().findViewById(R.id.spinnerFoodEditCategorySub);
            int intSpinnerSubCategory = spinnerSubCat.getSelectedItemPosition();

            String stringSpinnerSubCategoryName = spinnerSubCat.getSelectedItem().toString();

            //Find we want to find parent ID from the text
            String stringSpinnerSubCategoryNameSQL = db.quoteSmart(stringSpinnerSubCategoryName);

            String spinnerFields[] = new String[] {
                    "_id",
                    "category_name",
                    "category_parent_id"
            };

            Cursor findStringSpinnerSubCategoryID = db.select("categories", spinnerFields, "category_name",
                    stringSpinnerSubCategoryNameSQL);

            String stringSubCategoryID = findStringSpinnerSubCategoryID.getString(0).toString();
            String stringSubCategoryIDSQL = db.quoteSmart(stringSubCategoryID);

            String stringNameSQL = db.quoteSmart(stringName);
            String stringManufactorNameSQL = db.quoteSmart(stringManufactorName);
            String stringDescriptionSQL = db.quoteSmart(stringDescription);
            String stringBarcodeSQL = db.quoteSmart(stringBarcode);

            String stringServingSizeSQL = db.quoteSmart(stringServingSize);
            String stringServingMesurmentSQL = db.quoteSmart(stringServingMesurment);
            String stringServingNameNumberSQL = db.quoteSmart(stringServingNameNumber);
            String stringServingNameWordSQL = db.quoteSmart(stringServingNameWord);

            double doubleEnergyPerHundredSQL = db.quoteSmart(doubleEnergyPerHundred);
            double doubleProteinsPerHundredSQL = db.quoteSmart(doubleProteinsPerHundred);
            double doubleCarbsPerHundredSQL = db.quoteSmart(doubleCarbsPerHundred);
            double doubleFatPerHundredSQL = db.quoteSmart(doubleFatPerHundred);

            String stringEnergyPerHundredSQL = db.quoteSmart(stringEnergy);
            String stringProteinsPerHundredSQL = db.quoteSmart(stringProteins);
            String stringCarbsPerHundredSQL = db.quoteSmart(stringCarbohydrates);
            String stringFatPerHundredSQL = db.quoteSmart(stringFat);

            double energyCalculated = (doubleEnergyPerHundred * doubleServingSize) / 100;
            double proteinsCalculated = (doubleProteinsPerHundred * doubleServingSize) / 100;
            double carbsCalculated = (doubleCarbsPerHundred * doubleServingSize) / 100;
            double fatCalculated = (doubleFatPerHundred * doubleServingSize) / 100;

            String stringEnergyCalculated = "" + energyCalculated;
            String stringProteinsCalculated = "" + proteinsCalculated;
            String stringCarbsCalculated = "" + carbsCalculated;
            String stringFatCalculated = "" + fatCalculated;

            String stringEnergyCalculatedSQL = db.quoteSmart(stringEnergyCalculated);
            String stringProteinsCalculatedSQL = db.quoteSmart(stringProteinsCalculated);
            String stringCarbsCalculatedSQL = db.quoteSmart(stringCarbsCalculated);
            String stringFatCalculatedSQL = db.quoteSmart(stringFatCalculated);

            String fields =
                    " _id, " +
                    " food_name, " +
                    " food_manufactor_name, " +
                    " food_description, " +
                    " food_serving_size_gram, " +
                    " food_serving_size_gram_mesurment, " +
                    " food_serving_size_pcs, " +
                    " food_serving_size_pcs_mesurment, " +
                    " food_energy, " +
                    " food_proteins, " +
                    " food_carbohydrates, " +
                    " food_fat, " +
                    " food_energy_calculated, " +
                    " food_proteins_calculated, " +
                    " food_carbohydrates_calculated, " +
                    " food_fat_calculated, " +
                    " food_barcode, " +
                    " food_category_id";

            String values =
                    "NULL, " +
                    stringNameSQL + ", " +
                    stringManufactorNameSQL + ", " +
                    stringDescriptionSQL + ", " +
                    stringServingSizeSQL + ", " +
                    stringServingMesurmentSQL + ", " +
                    stringServingNameNumberSQL + ", " +
                    stringServingNameWordSQL + ", " +
                    stringEnergyPerHundredSQL + ", " +
                    stringProteinsPerHundredSQL + ", " +
                    stringCarbsPerHundredSQL + ", " +
                    stringFatPerHundredSQL + ", " +
                    stringEnergyCalculatedSQL + ", " +
                    stringProteinsCalculatedSQL + ", " +
                    stringCarbsCalculatedSQL + ", " +
                    stringFatCalculatedSQL + ", " +
                    stringBarcodeSQL + ", " +
                    stringSubCategoryIDSQL;

            db.insert("food", fields, values);
            Toast.makeText(getActivity(), "Food created", Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new FoodDietFragment(), FoodDietFragment.class.getName()).commit();

        }
        db.close();
    }

}

