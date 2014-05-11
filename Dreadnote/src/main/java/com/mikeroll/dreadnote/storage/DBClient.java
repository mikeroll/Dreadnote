package com.mikeroll.dreadnote.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mikeroll.dreadnote.entity.Note;

public class DBClient {

    private DBHelper mDBHelper;

    public DBClient(DBHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    public long createNote(Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(DBContract.Note.COL_TITLE, note.getTitle());
        entry.put(DBContract.Note.COL_COLOR, note.getColor());
        entry.put(DBContract.Note.COL_CONTENT, note.getContent());

        return db.insert(DBContract.Note.TABLE, null, entry);
    }

    public Note readNote(long id) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DBContract.Note.TABLE + " WHERE " + DBContract.Note._ID + " = ?",
                new String[] { Long.toString(id) }
        );
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            return new Note(cursor.getString(1), cursor.getInt(2), cursor.getString(3));
        } else {
            return null;
        }
    }

    public long updateNote(long id, Note note) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(DBContract.Note.COL_TITLE, note.getTitle());
        entry.put(DBContract.Note.COL_COLOR, note.getColor());
        entry.put(DBContract.Note.COL_CONTENT, note.getContent());

        return db.update(DBContract.Note.TABLE, entry, DBContract.Note._ID + " = ?", new String[] { Long.toString(id) });
    }

    public void deleteNote(long id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(DBContract.Note.TABLE, DBContract.Note._ID + " = ?", new String[] { Long.toString(id) });
    }
}
