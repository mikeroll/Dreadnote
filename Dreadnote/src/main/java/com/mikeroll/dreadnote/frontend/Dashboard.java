package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.storage.DBClient;
import com.mikeroll.dreadnote.storage.DBHelper;

public class Dashboard extends Activity {

    private DBClient mDBClient;
    private ResourceCursorAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFirstRunConfig();

        setContentView(R.layout.activity_dashboard);

        ListView noteList = (ListView) findViewById(R.id.note_list);
        noteListAdapter  = new ResourceCursorAdapter(this, R.layout.list_item, null, 0) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int color = cursor.getInt(2);
                ((TextView) view.findViewById(R.id.list_item_title)).setText(title);
                ((ImageView) view.findViewById(R.id.list_item_colorbox)).setImageDrawable(new ColorDrawable(color));
                view.setTag(id);
            }
        };
        noteList.setAdapter(noteListAdapter);
        noteList.setOnItemClickListener(new OnNoteClickListener());

        mDBClient = new DBClient(DBHelper.getInstance(getApplicationContext()));
    }

    private void performFirstRunConfig() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = prefs.getBoolean("isfirstrun", true);

        if (isFirstRun) {
            prefs.edit().putBoolean("isfirstrun", false).commit();
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        }
    }

    private void updateList() {
        Cursor c = mDBClient.selectAll(); //FIXME: This is very bad.
        noteListAdapter.changeCursor(c);
        noteListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_note:
                openNote(-1);
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }

    private void openNote(long id) {
        Intent intent = new Intent(Dashboard.this, NoteScreen.class);
        intent.putExtra(ExtrasNames.NOTE_ID, id);
        startActivity(intent);
    }

    private class OnNoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int id = (Integer)view.getTag();
            openNote(id);
        }
    }
}
