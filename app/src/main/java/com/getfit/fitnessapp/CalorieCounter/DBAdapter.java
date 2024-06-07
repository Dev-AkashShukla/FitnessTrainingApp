package com.getfit.fitnessapp.CalorieCounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    //Variables
    private static final String databaseName = "stramdiet";
    private static final int databaseVersion = 34;

    //Database Variables
    private final Context context;
    private DatabaseHelper DBHepler;
    private SQLiteDatabase db;

    //Class DBAdapter
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHepler = new DatabaseHelper(context);
    }

    //DatabaseHepler
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, databaseName, null, databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                //Create table goal
                db.execSQL("CREATE TABLE IF NOT EXISTS goal (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " goal_id INT," +
                        " goal_current_weight INT," +
                        " goal_target_weight INT," +
                        " goal_i_want_to VARCHAR," +
                        " goal_weekly_goal VARCHAR," +
                        " goal_date DATE," +
                        " goal_activity_level INT," +
                        " goal_energy_bmr INT," +
                        " goal_proteins_bmr INT," +
                        " goal_carbs_bmr INT," +
                        " goal_fat_bmr INT," +
                        " goal_energy_diet INT," +
                        " goal_proteins_diet INT," +
                        " goal_carbs_diet INT," +
                        " goal_fat_diet INT," +
                        " goal_energy_with_activity INT," +
                        " goal_proteins_with_activity INT," +
                        " goal_carbs_with_activity INT," +
                        " goal_fat_with_activity INT," +
                        " goal_energy_with_activity_and_diet INT," +
                        " goal_proteins_with_activity_and_diet INT," +
                        " goal_carbs_with_activity_and_diet INT," +
                        " goal_fat_with_activity_and_diet INT," +
                        " goal_notes VARCHAR);");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                //Create tables
                db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " user_id INT," +
                        " user_email VARCHAR," +
                        " user_password VARCHAR," +
                        " user_salt VARCHAR," +
                        " user_alias VARCHAR," +
                        " user_dob DATE," +
                        " user_gender INT," +
                        " user_location VARCHAR," +
                        " user_height INT," +
                        " user_activity_level INT," +
                        " user_mesurment VARCHAR," +
                        " user_last_seen TIME," +
                        " user_note VARCHAR);");

                db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_sum (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " food_diary_sum_id INT," +
                        " food_diary_sum_date DATE," +
                        " food_diary_sum_energy INT," +
                        " food_diary_sum_proteins INT," +
                        " food_diary_sum_carbs INT," +
                        " food_diary_sum_fat INT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_cal_eaten (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " fdce_id INT," +
                        " fdce_date DATE," +
                        " fdce_meal_no INT," +
                        " fdce_energy INT," +
                        " fdce_proteins INT," +
                        " fdce_carbs INT," +
                        " fdce_fat INT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS food_diary (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " fd_id INT," +
                        " fd_date DATE," +
                        " fd_meal_number INT," +
                        " fd_food_id INT," +
                        " fd_serving_size_gram DOUBLE," +
                        " fd_serving_size_gram_mesurment VARCHAR," +
                        " fd_serving_size_pcs DOUBLE," +
                        " fd_serving_size_pcs_mesurment VARCHAR," +
                        " fd_energy_calculated DOUBLE," +
                        " fd_protein_calculated DOUBLE," +
                        " fd_carbohydrates_calculated DOUBLE," +
                        " fd_fat_calculated DOUBLE," +
                        " fd_meal_id INT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " category_id INT," +
                        " category_name VARCHAR," +
                        " category_parent_id INT," +
                        " category_icon VARCHAR," +
                        " category_note VARCHAR);");

                db.execSQL("CREATE TABLE IF NOT EXISTS food (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " food_id INT," +
                        " food_name VARCHAR," +
                        " food_manufactor_name VARCHAR," +
                        " food_description VARCHAR," +
                        " food_serving_size_gram DOUBLE," +
                        " food_serving_size_gram_mesurment VARCHAR," +
                        " food_serving_size_pcs DOUBLE," +
                        " food_serving_size_pcs_mesurment VARCHAR," +
                        " food_energy DOUBLE," +
                        " food_proteins DOUBLE," +
                        " food_carbohydrates DOUBLE," +
                        " food_fat DOUBLE," +
                        " food_energy_calculated DOUBLE," +
                        " food_proteins_calculated DOUBLE," +
                        " food_carbohydrates_calculated DOUBLE," +
                        " food_fat_calculated DOUBLE," +
                        " food_user_id INT," +
                        " food_barcode VARCHAR," +
                        " food_category_id iNT," +
                        " food_thumb VARCHAR," +
                        " food_image_a VARCHAR," +
                        " food_image_b VARCHAR," +
                        " food_image_c VARCHAR," +
                        " food_last_used DATE," +
                        " food_notes VARCHAR);");
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            //! All tables that are going to be dropped need to be listed here
            db.execSQL("DROP TABLE IF EXISTS goal");
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS food_diary_sum");
            db.execSQL("DROP TABLE IF EXISTS food_diary_cal_eaten");
            db.execSQL("DROP TABLE IF EXISTS food_diary");
            db.execSQL("DROP TABLE IF EXISTS categories");
            db.execSQL("DROP TABLE IF EXISTS food");
            onCreate(db);

            String TAG = "Tag";
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                    newVersion + ", which will destroy all old data");
        } // end public void onUpgrade
    } //DatabaseHelper

    //Open database
    public DBAdapter open() throws SQLException {
        db = DBHepler.getWritableDatabase();
        return this;
    }

    //Close database
    public void close() {
        DBHepler.close();
    }

    //Quote smart
    public String quoteSmart(String value) {
        //Is numeric?
        boolean isNumeric = false;
        try {
            double myDouble = Double.parseDouble(value);
            isNumeric = true;
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        if (isNumeric == false) {
            if (value != null && value.length() > 0) {
                value = value.replace("\\", "\\\\");
                value = value.replace("'", "\\'");
                value = value.replace("\0", "\\0");
                value = value.replace("\n", "\\n");
                value = value.replace("\r", "\\r");
                value = value.replace("\"", "\\\"");
                value = value.replace("\\x1a", "\\z");
            }
        }

        value = "'" + value + "'";

        return value;
    }

    public  double quoteSmart(double value) {
        return value;
    }

    public int quoteSmart(int value) {
        return value;
    }

    public long quoteSmart(long value) {
        return value;
    }

    //Insert data
    public void insert(String table, String fields, String values) {
        db.execSQL("INSERT INTO " + table + "(" + fields + ") VALUES (" + values + ")");
    }

    //Count
    public int count(String table) {
        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + "", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    //Select

    public Cursor selectPrimaryKey(String table, String primaryKey, long rowId, String[] fields) throws SQLException {

        Cursor mCursor = db.query(table, fields, primaryKey + "=" + rowId, null, null, null, null,  null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Select
    public Cursor select(String table, String[] fields) throws SQLException {
        Cursor mCursor = db.query(table, fields, null, null, null, null, null,  null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Select All where(String)
    public Cursor select(String table, String[] fields, String whereClause, String whereCondition) throws SQLException {

        Cursor mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, null,  null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Select All where(String)
    public Cursor select(String table, String[] fields, String[] whereClause, String[] whereCondition, String[] whereAndOr) throws SQLException {

        String where = "";
        int arraySize = whereClause.length;
        for (int x = 0; x < arraySize; x++) {
            if (where.equals("")) {
                where = whereClause[x] + "=" + whereCondition[x];
            } else {
                where = where + " " + whereAndOr[x-1] + " " + whereClause[x] + "=" + whereCondition[x];
            }
        }

        Cursor mCursor = db.query(table, fields, where, null, null, null, null,  null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Select All where(Long)
    public Cursor select(String table, String[] fields, String whereClause, long whereCondition) throws SQLException {

        Cursor mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, null,  null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Select with order
    public Cursor select(String table, String[] fields, String whereClause,String whereCondition, String orderBy, String sortingOrder) {
        Cursor mCursor = null;

        if (whereClause.equals("")) {
            //We don't want to see where
            mCursor = db.query(table, fields, null, null, null, null, orderBy + " " + sortingOrder, null);
        } else {
            mCursor = db.query(table, fields, whereClause + "=" + whereCondition, null, null, null, orderBy + " " + sortingOrder, null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Update
    public boolean update(String table, String primaryKey, long rowId, String field, String value) {

        //Remove first and last value of value
        value = value.substring(1, value.length() - 1);

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }

    public boolean update(String table, String primaryKey, long rowId, String field, double value) {

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }

    public boolean update(String table, String primaryKey, long rowId, String field, int value) {

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }

    public boolean update(String table, String primaryKey, long rowId, String fields[], String values[]) {

        ContentValues args = new ContentValues();

        int arraySize = fields.length;
        for (int x = 0; x < arraySize; x++) {
            //Remove first and last value of value
            if (values[x].length() > 1) {
                values[x] = values[x].substring(1, values[x].length() - 1);
            }
            args.put(fields[x], values[x]);
        }

        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }

    //Delete
    //Delete a particular record
    public int delete(String table, String primaryKey, long rowID) throws SQLException {
        return db.delete(table, primaryKey + "=" + rowID, null);
    }

}
