package com.mikeroll.dreadnote.app.Storage;

import android.provider.BaseColumns;

public final class NoteContract {

    public NoteContract() {}

    public static abstract class Note implements BaseColumns {
        public static final String TABLE = "notes";
        public static final String COLUMN_ID = "id_note";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
    }

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Note.TABLE + " (" +
                    Note._ID + " INTEGER PRIMARY KEY," +
                    Note.COLUMN_ID + " INTEGER," +
                    Note.COLUMN_TITLE + " INTEGER," +
                    Note.COLUMN_CONTENT + " TEXT" +
            " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Note.TABLE;
}
