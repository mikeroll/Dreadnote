package com.mikeroll.dreadnote.frontend;

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
import android.os.Build;
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
import android.widget.PopupWindow;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.entity.Note;
import com.mikeroll.dreadnote.storage.DBClient;
import com.mikeroll.dreadnote.storage.DBHelper;
import org.jetbrains.annotations.NotNull;


public class NoteScreen extends Activity implements Editor.OnNoteChangeListener  {

    private static final int PAGES = 2;
    private static final int PREVIEW = 0;
    private static final int EDITOR = 1;

    private Preview preview = new Preview();
    private Editor editor = new Editor();

    // Preferences
    private boolean isStickyKeyboardEnabled;

    // Interface stuff
    private ViewPager mPager;
    private ColorChooser colorChooser;
    private EditText noteTitle;
    private Menu menu;
    private LayerDrawable bkg;

    // Entity/db stuff
    private long note_id;
    private Note note;

    public Note getNote() {
        return note;
    }

    private DBClient mDBClient;

    public DBClient getDBClient() {
        return mDBClient;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ModeSwitchAdapter(getFragmentManager()));
        mPager.setOnPageChangeListener(new OnModeSwitchListener());

        ActionBar ab = getActionBar();
        ab.setCustomView(R.layout.note_title);
        ab.setDisplayUseLogoEnabled(true);
        noteTitle = (EditText) ab.getCustomView().findViewById(R.id.note_title);
        noteTitle.setOnFocusChangeListener(new OnTitleFocusChangeListener());
        colorChooser = new ColorChooser(this);
        colorChooser.setOnColorChooseListener(new OnColorChooseListener());

        mDBClient = new DBClient(DBHelper.getInstance(getApplicationContext()));

        if (savedInstanceState != null) {
            note = savedInstanceState.getParcelable("note");
            int mode = savedInstanceState.getInt("mode");
            setMode(mode);
        } else {
            note_id = getIntent().getLongExtra(ExtrasNames.NOTE_ID, -1);
            if (note_id != -1) {
                note = mDBClient.readNote(note_id);
                setMode(PREVIEW);
            } else {
                note = new Note(getResources().getString(R.string.new_note_name));
                setMode(EDITOR);
            }
        }
        noteTitle.setText(note.getTitle());
        colorChooser.readCurrentColor(note.getColor());
        bkg = new LayerDrawable(new Drawable[] { new ColorDrawable(Color.WHITE), new ColorDrawable(note.getColor()) } );
        getWindow().getDecorView().setBackgroundDrawable(bkg);

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
            case R.id.action_choose_color:
                openColorChooser();
                return true;
            case R.id.action_switch_mode:
                switchPage();
                return true;
            case R.id.action_discard_changes:
                super.finish();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mPager.getCurrentItem());
        outState.putParcelable("note", note);
    }

    @Override
    public void finish() {
        noteTitle.clearFocus();
        mDBClient.addOrUpdateNote(note_id, note);
        super.finish();
    }

    private void setMode(int mode) {
        boolean editing = (mode == EDITOR);
        MenuItem pencil;
        if (menu != null && (pencil = menu.findItem(R.id.action_switch_mode)) != null) {
            pencil.setIcon(editing ? R.drawable.ic_action_accept : R.drawable.ic_action_edit);
        }
        noteTitle.setFocusable(editing);
        noteTitle.setFocusableInTouchMode(editing);
        noteTitle.setClickable(editing);
        Drawable bkg = noteTitle.getBackground();
        if (bkg != null) bkg.setAlpha(editing ? 0xFF : 0x00);
        mPager.setCurrentItem(mode);
    }

    private void showKeyboard(boolean show) {
        InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        if (show) {
            EditText edit = (EditText) findViewById(R.id.editor);
            if (edit != null) {
                edit.requestFocus();
                imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            imm.hideSoftInputFromWindow(mPager.getWindowToken(), 0);
        }
    }

    private void switchPage() {
        int current = mPager.getCurrentItem();
        int next = (current == PREVIEW) ? EDITOR : PREVIEW;
        setMode(next);
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }

    private void openColorChooser() {
        View anchor = findViewById(R.id.action_choose_color);
        View colorView = colorChooser.getContentView();
        colorView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        colorChooser.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        colorChooser.showAsDropDown(anchor, anchor.getWidth() - colorView.getMeasuredWidth(), 0);
    }

    @Override
    public void onNoteContentChanged(final String newData) {
        note.setContent(newData);
        Preview preview = (Preview) mPager.getAdapter().instantiateItem(mPager, 0);
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

    private class OnModeSwitchListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                showKeyboard(isStickyKeyboardEnabled && mPager.getCurrentItem() == EDITOR);
            }
        }

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == PREVIEW) {
                bkg.getDrawable(1).setAlpha((int) (0xFF * (1 - positionOffset)));
            } else /* if (position == EDITOR) */ {
                bkg.getDrawable(1).setAlpha((int) (0xFF * (positionOffset)));
            }
        }

        @Override public void onPageSelected(int position) {}
    }

    private class OnTitleFocusChangeListener implements EditText.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                Editable newTitle = ((EditText)view).getText();
                //TODO: expand validation
                if (newTitle != null && newTitle.length() != 0) {
                    note.setTitle(newTitle.toString());
                } else {
                    ((EditText)view).setText(note.getTitle());
                }
            }
        }
    }

    private class OnColorChooseListener implements ColorChooser.OnColorChooseListener {
        @Override
        public void onColorChoose(int color) {
            if (note.getColor() != color) {
                note.setColor(color);
                ColorDrawable cd = (ColorDrawable) bkg.getDrawable(1);
                int alpha = cd.getAlpha();
                cd.setColor(color);
                cd.setAlpha(alpha);
            }
        }
    }
}
