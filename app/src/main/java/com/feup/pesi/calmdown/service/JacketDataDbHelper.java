package com.feup.pesi.calmdown.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feup.pesi.calmdown.LocalDataContract;

public class JacketDataDbHelper extends SQLiteOpenHelper {
    private static JacketDataDbHelper instance;

    private static final String DATABASE_NAME = "jacket_data.db";
    private static final int DATABASE_VERSION = 1;

    private JacketDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized JacketDataDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new JacketDataDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the raw data table
        String SQL_CREATE_JACKET_DATA_TABLE = "CREATE TABLE " +
                LocalDataContract.LocalDataEntry.TABLE_NAME + " (" +
                LocalDataContract.LocalDataEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocalDataContract.LocalDataEntry.COLUMN_ADDRESS + " TEXT, " +
                LocalDataContract.LocalDataEntry.COLUMN_BATTERY_LEVEL + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_BPM + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_BPMI + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_NBYTES + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_NLEADS + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_POSITION + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_PULSE + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_RR + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_DATE_TIME_SPAN + " INTEGER, " +
                LocalDataContract.LocalDataEntry.COLUMN_USER_ID + " TEXT" +
                ");";

        String SQL_CREATE_AVERAGE_DATA_TABLE = "CREATE TABLE " +
                LocalDataContract.AverageDataEntry.TABLE_NAME + " (" +
                LocalDataContract.AverageDataEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_PULSE + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BATTERY_LEVEL + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_POSITION + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_RR + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BPMI + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BPM + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_NLEADS + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_NBYTES + " INTEGER, " +
                LocalDataContract.AverageDataEntry.COLUMN_USER_ID + " TEXT, " +
                LocalDataContract.AverageDataEntry.COLUMN_DATE_TIME + " INTEGER" +
                ");";

        // Execute SQL statements to create tables
        db.execSQL(SQL_CREATE_JACKET_DATA_TABLE);
        db.execSQL(SQL_CREATE_AVERAGE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrades (if needed)
    }
}
