package com.mikeroll.dreadnote.storage;

import android.provider.BaseColumns;

public class DBContract {

    private DBContract() {}

    public static abstract class Note implements BaseColumns {
        public static final String TABLE = "notes";
        public static final String COL_TITLE = "title";
        public static final String COL_COLOR = "color";
        public static final String COL_CONTENT = "content";
    }

}
