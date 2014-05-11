package com.mikeroll.dreadnote.storage;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DBContract.Note.TABLE + " (" +
                    DBContract.Note._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Note.COL_TITLE + " TEXT," +
                    DBContract.Note.COL_COLOR + " INTEGER," +
                    DBContract.Note.COL_CONTENT + " TEXT" +
            " )";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + DBContract.Note.TABLE;

    private static DBHelper mInstance;

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
