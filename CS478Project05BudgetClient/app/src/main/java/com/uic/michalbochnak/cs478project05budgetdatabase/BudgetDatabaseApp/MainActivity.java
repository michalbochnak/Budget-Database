//
// Michal Bochnak, mbochn2
// CS 478, May 1, 2018
// UIC
//


package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseApp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.BudgetDatabase;
import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.DailyCash;
import com.uic.michalbochnak.cs478project05budgetdatabase.R;
import java.util.ArrayList;


//
// Requests data from service and processes it
//
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BudgetDatabase budgetDb;
    private boolean isBound;
    private static final String PERMISSION =
            "com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseApp";

    private final ServiceConnection budgetDatabaseServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {
            budgetDb = BudgetDatabase.Stub.asInterface(iservice);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            budgetDb = null;
            isBound = false;
        }
    };

    private int month, day, year, range;
    private DailyCash[] dailyCash = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isBound) {
            boundToService();
        }
        setButtonListeners();
        ((Button)findViewById(R.id.queryDbButton)).setEnabled(false);
        month = day = year = range = -1;
    }


    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(this.budgetDatabaseServiceConn);
        }
        super.onDestroy();
    }

    private void setButtonListeners() {
        findViewById(R.id.createDbButton).setOnClickListener
                (new View.OnClickListener() {
                    public void onClick(View v) {
                        // create Db
                        try {
                            Log.i(TAG, "Creating Db...");
                            verifyPermission();
                            if (budgetDb.CreateDatabase())
                                ((Button)findViewById(R.id.queryDbButton)).setEnabled(true);
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                });

        findViewById(R.id.queryDbButton).setOnClickListener
                (new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            verifyPermission();
                            if (dateCorrect()) {
                                dailyCash = budgetDb.dailyCash(day, month, year, range);
                                if (dailyCash.length > 0) {
                                    startSecondActivity();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Nothing to show",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Date invalid", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void boundToService() {
        Intent intent = new Intent(BudgetDatabase.class.getName());
        ResolveInfo ri = getPackageManager().resolveService(intent, 0);
        intent.setComponent(new ComponentName(ri.serviceInfo.packageName,
                ri.serviceInfo.name));

        if (bindService(intent, this.budgetDatabaseServiceConn, Context.BIND_AUTO_CREATE)) {
            Log.i(TAG, "Service bound");
        }
        else {
            Log.i(TAG, "Service NOT bound");
        }
    }

    // used if permissions are dangerous
    private void verifyPermission() {
        // permission granted, proceed
        if (ContextCompat.checkSelfPermission(this, PERMISSION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted");
        }
        else {
            Log.i(TAG, "Asking OS for permission.");
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, 0);
        }
    }

    // used if permissions are dangerous
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
        if (results.length > 0) {
            // permission granted, proceed
            if (results[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted");
            }
            else {
                Log.i(TAG, "Permission NOT granted.");
            }
        }
    }

    //
    // Checks if date is valid
    //
    private boolean dateCorrect() {
        day = Integer.parseInt(((EditText)findViewById(R.id.dayEditText)).getText().toString());
        month = Integer.parseInt(((EditText)findViewById(R.id.monthEditText)).getText().toString());
        year = Integer.parseInt(((EditText)findViewById(R.id.yearEditText)).getText().toString());
        range = Integer.parseInt(((EditText)findViewById(R.id.rangeEditText)).getText().toString());

        if (year < 2017  || year > 2018)
            return false;
        else if (month < 1 || month > 12)
            return false;
        else if (day < 1 || day > 31)
            return false;
        else if (range < 1 || range > 30)
            return false;

        return true;
    }

    private void startSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        ArrayList<DailyCash> array = new ArrayList<DailyCash>();

        for (int i = 0; i < dailyCash.length; ++i)
            array.add(dailyCash[i]);

        intent.putExtra("list", array);
        startActivity(intent);
    }
}
