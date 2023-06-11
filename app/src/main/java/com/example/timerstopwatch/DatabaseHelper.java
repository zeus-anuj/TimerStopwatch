package com.example.timerstopwatch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "occurrences.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "occurrences";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_OCCURRENCES = "CREATE TABLE " +
            TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TYPE + " TEXT," +
            COLUMN_TIME + " INTEGER," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_OCCURRENCES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
