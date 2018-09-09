//
// Michal Bochnak, mbochn2
// CS 478, May 1, 2018
// UIC
//


package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon;
import com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon.DailyCash;


interface BudgetDatabase {
    boolean CreateDatabase();
    DailyCash[] dailyCash(int day, int month, int year, int numWorkingDays);
}
