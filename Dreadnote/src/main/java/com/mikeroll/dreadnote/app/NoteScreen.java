package com.mikeroll.dreadnote.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class NoteScreen extends FragmentActivity implements Editor.OnNoteChangedListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ModeSwitchAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.switch_mode) {
            switchMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "LOL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchMode() {
        int current = mPager.getCurrentItem();
        int next = (current == 0) ? 1 : 0;
        mPager.setCurrentItem(next);
    }

    private void showKeyboard() {
        EditText edit = (EditText) findViewById(R.id.editor);
        if (edit != null) {
            edit.requestFocus();
            InputMethodManager imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imgr.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText edit = (EditText) findViewById(R.id.editor);
        if (edit != null)
            imgr.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    private class ModeSwitchAdapter extends FragmentStatePagerAdapter {

        private static final int PAGES = 2;

        public ModeSwitchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new Preview();
            } else if (position == 1) {
                return new Editor();
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGES;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (position == 1) {
                showKeyboard();
            } else {
                hideKeyboard();
            }
        }
    }
}
