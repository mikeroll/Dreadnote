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

public class Dashboard extends Activity {

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

        i.putExtra(ExtrasNames.NOTE, "note0.note");
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }
}
