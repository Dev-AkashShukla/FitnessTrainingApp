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
import android.widget.Toast;

import com.getfit.fitnessapp.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesDietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesDietFragment extends  Fragment implements
        NavigationView.OnNavigationItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Cursor listCursorCategory;
    private Cursor listCursorFood;
    private View mainView;

    //Action buttons on toolbar
    private MenuItem menuItemEdit;
    private MenuItem menuItemDelete;

    //Holder for buttons on toolbar
    private String currentCategoryId;
    private String currentCategoryName;

    private String currentFoodId;
    private String currentFoodName;

    //01 Constructor
    public CategoriesDietFragment() {
        // Required empty public constructor
    }

    //02 Creating Fragment
    public static CategoriesDietFragment newInstance(String param1, String param2) {
        CategoriesDietFragment fragment = new CategoriesDietFragment();
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

        ((DietActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        //Populate the list of categories
        populateList("0","");

        //Create menu
        setHasOptionsMenu(true);
    }

    //04 On create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_categories_diet, container, false);
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ((DietActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_categories, menu);

        // Assign menu items to variables
        menuItemEdit = menu.findItem(R.id.menu_action_categories_edit);
        menuItemDelete = menu.findItem(R.id.menu_action_categories_delete);

        //Hide as default
        menuItemEdit.setVisible(false);
        menuItemDelete.setVisible(false);

    }

    //05 On Options Item Selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_action_categories_add) {
            createNewCategory();
        }
        if (id == R.id.menu_action_categories_edit) {
            editCategory();
        }
        if (id == R.id.menu_action_categories_delete) {
            deleteCategory();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    //06 Our own methods

    //Create new category
    private void createNewCategory() {
        //Change layout
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Fill spinner with categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        //Creating array
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount + 1];

        //This is parent
        arraySpinnerCategories[0] = "-";

        //Convert Cursor to String
        for (int x = 1; x < dbCursorCount + 1; x++) {
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();
            dbCursor.moveToNext();
        }

        //Populate spinner
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoriesParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);
//        Toast.makeText(getActivity(), spinnerParent.getCount(), Toast.LENGTH_SHORT).show();


        Button buttonCategoriesSubmit = (Button) getActivity().findViewById(R.id.button_categories_add_edit);
        buttonCategoriesSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCategorySubmitOnClick();
            }
        });

        db.close();

    }

    private void createNewCategorySubmitOnClick() {

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Name
        EditText editTextName = (EditText) getActivity().findViewById(R.id.editText_categories_add_edit_name);
        String stringName = editTextName.getText().toString();

        String parentID;

        if (TextUtils.isEmpty(stringName)) {
            Toast.makeText(getActivity(), "Category name is needed", Toast.LENGTH_SHORT).show();
            editTextName.setError("Please fill in a name");
            editTextName.requestFocus();
        } else {

            //Parent
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerCategoriesParent);
            String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();

            if (stringSpinnerCategoryParent.equals("-")) {
                parentID = "0";
            } else {
                //Find we want to find parent ID from the text
                String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);

                String fields[] = new String[] {
                        "_id",
                        "category_name",
                        "category_parent_id"
                };

                Cursor findParentID = db.select("categories", fields, "category_name",
                        stringSpinnerCategoryParentSQL);

                parentID = findParentID.getString(0).toString();
            }

            //Ready variables
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            //Insert into database
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
            db.insert("categories", "_id, category_name, category_parent_id", input);

            //Give feedback
            Toast.makeText(getActivity(), "Category created", Toast.LENGTH_SHORT).show();

            //Move user back to correct design
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesDietFragment(), CategoriesDietFragment.class.getName()).commit();
        }

        db.close();

    }

    //Populate List
    public void populateList(String parentID, String parentName) {

        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        listCursorCategory = db.select("categories", fields, "category_parent_id", parentID, "category_name", "ASC");

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
        ListView listView = (ListView)getActivity().findViewById(R.id.listViewCategories);
        listView.setAdapter(adapter);

        //OnClick
        if (parentID.equals("0")) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemClicked(position);
                }
            });
        }

        db.close();

        //Remove or show edit button
        if (parentID.equals("0")) {
            //Remove edit button

//            menuItemEdit.setVisible(false);
//            menuItemDelete.setVisible(false);
        } else {
            //Show edit button
            menuItemEdit.setVisible(true);
            menuItemDelete.setVisible(true);
        }

    }

    //List item clicked
    public void listItemClicked(int position) {

        //Move cursor to ID clicked
        listCursorCategory.moveToPosition(position);

        currentCategoryId = listCursorCategory.getString(0);
        currentCategoryName = listCursorCategory.getString(1);
        String parentID = listCursorCategory.getString(2);

        //Change title
        ((DietActivity)getActivity()).getSupportActionBar().setTitle(currentCategoryName);

        //Move to sub class
        populateList(currentCategoryId, currentCategoryName);

        //Show food in category
        showFoodInCategory(currentCategoryId, currentCategoryName, parentID);
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
            ListView listViewFood = (ListView)getActivity().findViewById(R.id.listViewFood);

            //Setup cursor adapter using cursor from last step
            FoodCursorAdapter cursorAdapter = new FoodCursorAdapter(getActivity(), listCursorFood);

            listViewFood.setAdapter(cursorAdapter);

            //OnClick

            listViewFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    foodListItemClicked(position);
                }
            });


            db.close();
        }
    }

    //Food list item clicked
    private void foodListItemClicked(int intFoodListItemIndex) {

        //We should use
        currentFoodId = listCursorFood.getString(0);
        currentFoodName = listCursorFood.getString(1);

        Toast.makeText(getActivity(), currentFoodName, Toast.LENGTH_SHORT).show();

//        //Change fragment to foodView
//        //Initialize fragment
//        Fragment fragment = null;
//        Class fragmentClass = null;
//        fragmentClass = AddFoodToDiaryFragment.class;
//
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //Send variable
//        Bundle bundle = new Bundle();
//        bundle.putString("mealNumber", "" + mealNumber);
//        fragment.setArguments(bundle);
//
//        //Need to pass meal number
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    //Edit category
    private void editCategory() {
        //Edit Name = currentName
        //Edit ID = currentID

        //Change layout
        int id = R.layout.fragment_categories_add_edit;
        setMainView(id);

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Ask for parent ID
        Cursor c;
        String fieldsC[] = new String[] {"category_parent_id"};
        String currentIdSQL = db.quoteSmart(currentCategoryId);
        c = db.select("categories", fieldsC, "_id", currentIdSQL);
        String currentParentID = c.getString(0);
        int intCurrentParentID = 0;
        try {
            intCurrentParentID = Integer.parseInt(currentParentID);
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        //Fill name
        EditText editTextName = (EditText)getActivity().findViewById(R.id.editText_categories_add_edit_name);
        editTextName.setText(currentCategoryName);

        //Fill spinner with categories
        String fields[] = new String[] {
                "_id",
                "category_name",
                "category_parent_id"
        };
        Cursor dbCursor = db.select("categories", fields, "category_parent_id", "0", "category_name", "ASC");

        //Creating array
        int dbCursorCount = dbCursor.getCount();
        String[] arraySpinnerCategories = new String[dbCursorCount + 1];

        //This is parent
        arraySpinnerCategories[0] = "-";

        //Convert Cursor to String
        int correctParentID = 0;
        for (int x = 1; x < dbCursorCount + 1; x++) {
            arraySpinnerCategories[x] = dbCursor.getString(1).toString();

            if (dbCursor.getString(0).toString().equals(currentParentID)) {
                correctParentID = x;
            }

            //Move to next
            dbCursor.moveToNext();
        }

        //Populate spinner
        Spinner spinnerParent = (Spinner) getActivity().findViewById(R.id.spinnerCategoriesParent);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinnerCategories);
        spinnerParent.setAdapter(adapter);

        //Select correct spinner item, that is the parent to currentID
        spinnerParent.setSelection(correctParentID);

        //Close db
        db.close();

        Button buttonCategoriesSubmit = (Button) getActivity().findViewById(R.id.button_categories_add_edit);
        buttonCategoriesSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategorySubmitOnClick();
            }
        });


    }

    private void editCategorySubmitOnClick() {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Name
        EditText editTextName = (EditText) getActivity().findViewById(R.id.editText_categories_add_edit_name);
        String stringName = editTextName.getText().toString();

        String parentID;

        if (TextUtils.isEmpty(stringName)) {
            Toast.makeText(getActivity(), "Category name is needed", Toast.LENGTH_SHORT).show();
            editTextName.setError("Please fill in a name");
            editTextName.requestFocus();
        } else {
            //Parent
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerCategoriesParent);
            String stringSpinnerCategoryParent = spinner.getSelectedItem().toString();

            if (stringSpinnerCategoryParent.equals("-")) {
                parentID = "0";
            } else {
                //Find we want to find parent ID from the text
                String stringSpinnerCategoryParentSQL = db.quoteSmart(stringSpinnerCategoryParent);

                String fields[] = new String[] {
                        "_id",
                        "category_name",
                        "category_parent_id"
                };

                Cursor findParentID = db.select("categories", fields, "category_name",
                        stringSpinnerCategoryParentSQL);

                parentID = findParentID.getString(0).toString();
            }

            //Current ID to long
            long longCurrentID = Long.parseLong(currentCategoryId);

            //Ready variables
            long currentIDSQL = db.quoteSmart(longCurrentID);
            String stringNameSQL = db.quoteSmart(stringName);
            String parentIDSQL = db.quoteSmart(parentID);

            //Insert into database
            String input = "NULL, " + stringNameSQL + ", " + parentIDSQL;
//            db.insert("categories", "_id, category_name, category_parent_id", input);
            db.update("categories", "_id", currentIDSQL, "category_name", stringNameSQL);
            db.update("categories", "_id", currentIDSQL, "category_parent_id", parentIDSQL);

            //Give feedback
            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();

            //Move user back to correct design
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesDietFragment(), CategoriesDietFragment.class.getName()).commit();
        }

        db.close();
    }

    //Delete category
    private void deleteCategory() {

        //Change layout
        int id = R.layout.fragment_categories_delete;
        setMainView(id);

        Button buttonCancel = (Button) getActivity().findViewById(R.id.button_categories_delete_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryCancelOnClick();
            }
        });

        Button buttonConfirmDelete = (Button) getActivity().findViewById(R.id.button_categories_delete);
        buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryConfirmOnClick();
            }
        });
    }

    private void deleteCategoryConfirmOnClick() {
        //Delete from SQL

        //Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Current ID to long
        long longCurrentID = Long.parseLong(currentCategoryId);

        //Ready variables
        long currentIDSQL = db.quoteSmart(longCurrentID);

        //Delete
        db.delete("categories", "_id", currentIDSQL);

        //Close db
        db.close();

        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_LONG).show();

        //Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesDietFragment(), CategoriesDietFragment.class.getName()).commit();
    }

    private void deleteCategoryCancelOnClick() {
        //Move user back to correct design
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new CategoriesDietFragment(), CategoriesDietFragment.class.getName()).commit();
    }
}