package com.feup.pesi.calmdown.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.feup.pesi.calmdown.LocalDataContract;
import com.feup.pesi.calmdown.model.LocalData;

public class JacketDataDbAdapter {
    private static SQLiteDatabase database;
    private JacketDataDbHelper dbHelper;

    public JacketDataDbAdapter(Context context) {
        dbHelper = JacketDataDbHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insert a LocalData object into the database
    public static long insertLocalData(LocalData jacketData) {
        ContentValues values = new ContentValues();
        values.put(LocalDataContract.LocalDataEntry.COLUMN_ADDRESS, jacketData.getAddress());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_BATTERY_LEVEL, jacketData.getBatteryLevel());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_BPM, jacketData.getBpm());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_BPMI, jacketData.getBpmi());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_NBYTES, jacketData.getBytes());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_NLEADS, jacketData.getNleads());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_POSITION, jacketData.getPosition());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_PULSE, jacketData.getPulse());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_RR, jacketData.getRr());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_DATE_TIME_SPAN, jacketData.getDateTimeSpan().getTime());
        values.put(LocalDataContract.LocalDataEntry.COLUMN_USER_ID, jacketData.getUserId());

        // Perform the insert
        return database.insert(LocalDataContract.LocalDataEntry.TABLE_NAME, null, values);
    }

    // Retrieve all LocalData records from the database
    public Cursor getAllLocalData() {
        return database.query(
                LocalDataContract.LocalDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    // Insert average data into the database
    public static long insertAverageData(int averagePulse, int averageBattery, long averagePosition,
                                         int averageRr, int averageBpmi, int averageBpm, int averageNleads,
                                         int averageNbytes, String userId, long dateTime) {
        ContentValues values = new ContentValues();
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_PULSE, averagePulse);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BATTERY_LEVEL, averageBattery);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_POSITION, averagePosition);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_RR, averageRr);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BPMI, averageBpmi);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_BPM, averageBpm);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_NLEADS, averageNleads);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_AVERAGE_NBYTES, averageNbytes);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_USER_ID, userId);
        values.put(LocalDataContract.AverageDataEntry.COLUMN_DATE_TIME, dateTime);

        // Perform the insert
        return database.insert(LocalDataContract.AverageDataEntry.TABLE_NAME, null, values);
    }

    // Retrieve all average data records from the database
    public Cursor getAllAverageData() {
        return database.query(
                LocalDataContract.AverageDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }




}

