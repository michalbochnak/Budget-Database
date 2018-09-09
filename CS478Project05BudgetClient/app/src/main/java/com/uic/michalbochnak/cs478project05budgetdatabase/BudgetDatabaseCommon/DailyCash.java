//
// Michal Bochnak, mbochn2
// CS 478, May 1, 2018
// UIC
//


package com.uic.michalbochnak.cs478project05budgetdatabase.BudgetDatabaseCommon;

import android.os.Parcel;
import android.os.Parcelable;


public class DailyCash implements Parcelable {

    int mDay;
    int mMonth;
    int mYear;
    int mCash;
    String mDayOfWeek;

    public DailyCash(String year,  String month, String day, String dayOfWeek, String closing) {
        mDay = Integer.parseInt(day);
        mMonth = Integer.parseInt(month);
        mYear = Integer.parseInt(year);
        mCash = Integer.parseInt(closing);
        mDayOfWeek = dayOfWeek;
    }

    public DailyCash(Parcel in) {
        mDay = in.readInt() ;
        mMonth = in.readInt() ;
        mYear = in.readInt() ;
        mCash = in.readInt() ;
        mDayOfWeek = in.readString() ;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mDay);
        out.writeInt(mMonth) ;
        out.writeInt(mYear) ;
        out.writeInt(mCash) ;
        out.writeString(mDayOfWeek) ;
    }

    public static final Parcelable.Creator<DailyCash> CREATOR
            = new Parcelable.Creator<DailyCash>() {

        public DailyCash createFromParcel(Parcel in) {
            return new DailyCash(in) ;
        }

        public DailyCash[] newArray(int size) {
            return new DailyCash[size];
        }
    };

    public int describeContents()  {
        return 0 ;
    }

    public String getStringToDisplay() {
        return mYear + "/" + mMonth + "/" + mDay + " (" + mDayOfWeek + "): $" + mCash;
    }

}