//
// Michal Bochnak, mbochn2
// CS 478, May 1, 2018
// UIC
//


package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseApp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.BudgetDatabase;
import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.DailyCash;
import com.uic.michalbochnak.cs478project05budgetdatabase.R;


//
// Service to create database and provide the client with requested data
//
public class BudgetDatabaseImpl extends Service {

    private static final String TAG = "BudgetDatabaseImpl";
    private BudgetDatabaseHelper dbHelper;
    private Context context = this;

    // Implement the Stub for this Object
    private final BudgetDatabase.Stub mBinder = new BudgetDatabase.Stub() {

        public synchronized boolean CreateDatabase() {
            Log.i(TAG, "CreateDatabase()");
            dbHelper = new BudgetDatabaseHelper(context);
            addEntriesFromFile();
            return isDbCreated();
        }

        //
        // Return balance for requested dates
        //
        public synchronized DailyCash[] dailyCash(int day, int month,
                  int year, int numWorkingDays) {

            // get the starting row id
            Cursor cursor01 = getMyCursor(year, month, day);

            if (cursor01.moveToFirst()) {

                int startRowId = cursor01.getInt(0);
                cursor01.close();

                // get requested data
                String sql2 = "SELECT * FROM " + BudgetDatabaseHelper.TABLE_NAME
                        + " WHERE " + BudgetDatabaseHelper.ID + " >= " + startRowId
                        + " LIMIT " + numWorkingDays ;

                Cursor cursor02 = dbHelper.getReadableDatabase().rawQuery(sql2, null);

                // create array to be returned
                DailyCash[] dc = new DailyCash[cursor02.getCount()];
                int index = 0;
                if (cursor02.moveToFirst()) {
                    do {
                        dc[index++] = new DailyCash(cursor02.getString(1),
                                cursor02.getString(2),
                                cursor02.getString(3),
                                cursor02.getString(4),
                                cursor02.getString(6));
                    } while (cursor02.moveToNext());
                }
                cursor02.close();

                return dc;
            }
            else {
                return new DailyCash[0];
            }
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void addEntriesFromFile() {
        // clear old Db
        dbHelper.deleteDatabase();
        BufferedReader br;
        InputStream is = getResources().openRawResource(R.raw.treasury_io_final);
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                addEntryToDb(line);
            }
        } catch(Exception exc1) {
        }
    }

    private void addEntryToDb(String line) throws IOException {
        try {
            String[] parts = line.split("\\,");
            ContentValues vals = new ContentValues();
            vals.put(BudgetDatabaseHelper.YEAR, parts[0]);
            vals.put(BudgetDatabaseHelper.MONTH, parts[1]);
            vals.put(BudgetDatabaseHelper.DAY, parts[2]);
            vals.put(BudgetDatabaseHelper.DAY_NAME, parts[3]);
            vals.put(BudgetDatabaseHelper.OPENING, parts[4]);
            vals.put(BudgetDatabaseHelper.CLOSING, parts[5]);

            dbHelper.getWritableDatabase().insert
                    (BudgetDatabaseHelper.TABLE_NAME, null, vals);
        }
        catch (Exception exc) {
            throw new IOException("Db Update failed");
        };
    }

    private boolean isDbCreated() {
        Cursor cursor = dbHelper.getReadableDatabase().query(BudgetDatabaseHelper.TABLE_NAME,
                BudgetDatabaseHelper.columns, null, new String[] {},
                null, null, null);

        return cursor.moveToFirst();
    }

    private Cursor getMyCursor(int y, int m, int d) {
        Cursor cursor;
        int year = y;
        int month = m;
        int day = d;

        String sql = "SELECT " + BudgetDatabaseHelper.ID
                + " FROM " + BudgetDatabaseHelper.TABLE_NAME
                + " WHERE " + BudgetDatabaseHelper.YEAR + " = " + year
                + " AND " + BudgetDatabaseHelper.MONTH + " = " + month
                + " AND " + BudgetDatabaseHelper.DAY + " = " + day;

        cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);

        if (cursor.moveToFirst())
            return cursor;

        for (int i = 0; i < 10; i++) {

            sql = "SELECT " + BudgetDatabaseHelper.ID
                    + " FROM " + BudgetDatabaseHelper.TABLE_NAME
                    + " WHERE " + BudgetDatabaseHelper.YEAR + " = " + year
                    + " AND " + BudgetDatabaseHelper.MONTH + " = " + month
                    + " AND " + BudgetDatabaseHelper.DAY + " = " + day;

            cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);

            if (cursor.moveToFirst())
                return cursor;

            // increment data
            try {
                String dt = year + "-" + month + "-" + day;  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(dt));
                c.add(Calendar.DATE, 1);  // number of days to add
                dt = sdf.format(c.getTime());  // dt is now the new date
                year = Integer.parseInt(dt.substring(0, 4));
                month = Integer.parseInt(dt.substring(5, 7));
                day = Integer.parseInt(dt.substring(8, 10));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return cursor;
    }

}
