package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.mikeroll.dreadnote.R;


public class Dashboard extends Activity {

    public static final int REQUEST_OPEN_NOTE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onBtn(View view) {
        Intent i = new Intent(this, NoteScreen.class);
        i.putExtra(ExtrasNames.NOTE_CONTENT, getResources().getString(R.string.debug_string));
        startActivityForResult(i, REQUEST_OPEN_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_NOTE) {
            String newData = data.getStringExtra(ExtrasNames.NOTE_CONTENT);
            Toast.makeText(this, newData, Toast.LENGTH_LONG).show(); //TODO: this is debugging
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
