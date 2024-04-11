package com.example.mapsforevents;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMAGE_FILENAME = "image_filename";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_EVENT_NAME = "event_name";

    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COLUMN_ID = "id";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "events_database";

    private static final String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EVENT_NAME + " TEXT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_SUMMARY + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_CONTACT + " TEXT,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_IMAGE_FILENAME + " TEXT,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL"
            + ")";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(db);
    }

}
