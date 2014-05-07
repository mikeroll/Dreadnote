package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.storage.Note;


public class Dashboard extends Activity {

    public static final int REQUEST_OPEN_NOTE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        performFirstRunConfig();
    }

    private void performFirstRunConfig() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = prefs.getBoolean("isfirstrun", true);

        if (isFirstRun) {
            prefs.edit().putBoolean("isfirstrun", false).commit();
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        }
    }

    public void onBtn(View view) {
        Intent i = new Intent(this, NoteScreen.class);
        Note note = new Note("Welcome!", getResources().getColor(R.color.note_green),
                getResources().getString(R.string.debug_string));
        i.putExtra(ExtrasNames.NOTE, note);
        startActivityForResult(i, REQUEST_OPEN_NOTE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
