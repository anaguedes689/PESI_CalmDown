package com.feup.pesi.calmdown;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JacketDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jacket.db";
    private static final int DATABASE_VERSION = 1;

    public JacketDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE jacket ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "address TEXT, "
                + "userId TEXT, "
                + "rr INTEGER, "
                + "pulse INTEGER, "
                + "batteryLevel INTEGER, "
                + "position INTEGER, "
                + "bpmi INTEGER, "
                + "bpm INTEGER, "
                + "nleads INTEGER, "
                + "nBytes INTEGER, "
                + "dateTimeSpan TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS jacket");
        onCreate(db);
    }
}