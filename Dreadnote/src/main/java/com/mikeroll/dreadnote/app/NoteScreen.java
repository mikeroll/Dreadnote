package com.mikeroll.dreadnote.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class NoteScreen extends Activity {

    private enum Mode { PREVIEW, EDITOR };
    private Mode mode;
    private static final String PREVIEW_TAG = "PREVIEW";
    private static final String EDITOR_TAG = "EDITOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);
        if (savedInstanceState != null) {
            return;
        }

        getFragmentManager().beginTransaction()
                .add(R.id.note_screen, new Preview(), PREVIEW_TAG)
                .commit();
        mode = Mode.PREVIEW;

        setNoteColor(0xFFFFF8DC); //temporary!
    }

    public void setNoteColor(int color) {
        this.getWindow().getDecorView().setBackgroundColor(color);
        assert getActionBar() != null;
        getActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.switch_mode) {
            switchMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchMode() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment preview, editor;
        if (mode == Mode.PREVIEW) {
            editor = fm.findFragmentByTag(EDITOR_TAG);
            if (editor == null) {
                editor = new Editor();
            }
            ft.replace(R.id.note_screen, editor);
            mode = Mode.EDITOR;
        } else if (mode == Mode.EDITOR) {
            preview = fm.findFragmentByTag(PREVIEW_TAG);
            if (preview == null) {
                preview = new Preview();
            }
            ft.replace(R.id.note_screen, preview);
            mode = Mode.PREVIEW;
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
