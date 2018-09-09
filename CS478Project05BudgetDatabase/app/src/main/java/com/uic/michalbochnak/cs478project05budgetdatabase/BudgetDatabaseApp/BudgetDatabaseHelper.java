package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//
// Helper to support Db operation
//
public class BudgetDatabaseHelper extends SQLiteOpenHelper {

    final static String TABLE_NAME = "budget";
    final static String ID = "id";
    final static String YEAR = "year";
    final static String MONTH = "month";
    final static String DAY = "day";
    final static String DAY_NAME = "day_name";
    final static String OPENING = "opening";
    final static String CLOSING = "closing";
    final static String[] columns = {ID, YEAR, MONTH, DAY, DAY_NAME, OPENING, CLOSING};

    final private static String CREATE_DB_CMD =

            "CREATE TABLE budget (" + ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + YEAR + " TEXT NOT NULL, "
                    + MONTH + " TEXT NOT NULL, "
                    + DAY + " TEXT NOT NULL, "
                    + DAY_NAME + " TEXT NOT NULL, "
                    + OPENING + " TEXT NOT NULL, "
                    + CLOSING + " TEXT NOT NULL)";

    final private static String NAME = "budget_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public BudgetDatabaseHelper(Context context) {
        // Always call superclass's constructor
        super(context, NAME, null, VERSION);

        // Save the context that created DB in order to make calls on that context,
        // e.g., deleteDatabase() below.
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    // Calls ContextWrapper.deleteDatabase() by way of inheritance
    public void deleteDatabase() {
        mContext.deleteDatabase(NAME);
    }


}
