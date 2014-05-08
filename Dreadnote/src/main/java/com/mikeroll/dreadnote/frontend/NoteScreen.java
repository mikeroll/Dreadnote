package com.mikeroll.dreadnote.frontend;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.storage.Note;


public class NoteScreen extends Activity implements Editor.OnNoteChangedListener  {

    private static final int PAGES = 2;
    private static final int PREVIEW = 0;
    private static final int EDITOR = 1;

    private Preview preview = new Preview();
    private Editor editor = new Editor();

    private ViewPager mPager;
    private ModeSwitchAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                setMode(mPager.getCurrentItem());
            }
        }

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == PREVIEW) {
                ld.getDrawable(1).setAlpha((int) (0xFF * (1 - positionOffset)));
            } else /* if (position == EDITOR) */ {
                ld.getDrawable(1).setAlpha((int) (0xFF * (positionOffset)));
            }
            rootView.setBackgroundDrawable(ld);
        }

        @Override public void onPageSelected(int position) {}
    };

    // Preferences
    private boolean isStickyKeyboardEnabled;

    // Interface stuff
    private View rootView;
    private ColorDrawable previewColor;
    private LayerDrawable ld;
    private EditText noteTitle;
    private Menu menu;

    private Note note;

    public Note getNote() {
        return note;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);

        note = getIntent().getParcelableExtra(ExtrasNames.NOTE);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setCustomView(R.layout.note_title);
            ab.setDisplayUseLogoEnabled(true);
            noteTitle = (EditText) ab.getCustomView().findViewById(R.id.note_title);
            noteTitle.setText(note.getTitle());
            noteTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        Editable newTitle = noteTitle.getText();
                        //TODO: expand validation
                        if (newTitle != null && newTitle.length() != 0) {
                            note.setTitle(newTitle.toString());
                        } else {
                            noteTitle.setText(note.getTitle());
                        }
                    }
                }
            });
        }

        previewColor = new ColorDrawable(note.getColor());
        ld = new LayerDrawable(new Drawable[]{new ColorDrawable(Color.WHITE), previewColor});
        rootView = getWindow().getDecorView();
        rootView.setBackgroundDrawable(ld);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ModeSwitchAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(mPageChangeListener);

        if (savedInstanceState != null) {
            int mode = savedInstanceState.getInt("mode");
            mPager.setCurrentItem(mode);
            setMode(mode);
        } else {
            mPager.setCurrentItem(PREVIEW);
            setMode(PREVIEW);
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isStickyKeyboardEnabled = prefs.getBoolean(getString(R.string.pref_sticky_keyboard), true);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_screen, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.switch_page:
                switchPage();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mPager.getCurrentItem());
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(ExtrasNames.NOTE, note));
        super.finish();
    }

    private void setMode(int mode) {
        if (mode == PREVIEW) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mPager.getWindowToken(), 0);
            if (menu != null) menu.getItem(0).setIcon(R.drawable.ic_action_edit);
        } else /* if (mode == EDITOR) */ {
            EditText edit = (EditText) findViewById(R.id.editor);
            if (isStickyKeyboardEnabled && edit != null) {
                edit.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
            }
            if (menu != null) menu.getItem(0).setIcon(R.drawable.ic_action_accept);
        }
        boolean editing = (mode == EDITOR);
        noteTitle.setFocusable(editing);
        noteTitle.setFocusableInTouchMode(editing);
        noteTitle.setClickable(editing);
        Drawable bkg = noteTitle.getBackground();
        if (bkg != null) bkg.setAlpha(editing ? 0xFF : 0x00);
    }

    private void switchPage() {
        int current = mPager.getCurrentItem();
        int next = (current == PREVIEW) ? EDITOR : PREVIEW;
        mPager.setCurrentItem(next);
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }

    @Override
    public void onNoteContentChanged(final String newData) {
        note.setContent(newData);
        Preview preview = (Preview) mPagerAdapter.instantiateItem(mPager, 0);
        preview.updateNotePresentation(newData);
    }

    private class ModeSwitchAdapter extends FragmentStatePagerAdapter {

        public ModeSwitchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == PREVIEW) {
                return preview;
            } /* else if (position == EDITOR) */
                return editor;
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }
}
