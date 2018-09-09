//
// Michal Bochnak, mbochn2
// CS 478, May 1, 2018
// UIC
//

package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseApp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.DailyCash;

import java.util.ArrayList;


//
// List view to display results
//
public class SecondActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        ArrayList<DailyCash> arrayList = intent.getParcelableArrayListExtra("list");
        String[] list = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); ++i) {
            list[i] = arrayList.get(i).getStringToDisplay();
        }
        ListAdapter adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);
    }
}
