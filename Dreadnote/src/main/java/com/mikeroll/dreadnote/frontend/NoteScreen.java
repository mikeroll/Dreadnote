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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.storage.Note;


public class NoteScreen extends Activity implements Editor.OnNoteChangedListener  {

    private static final int PAGES = 2;
    private static final int PREVIEW_POS = 0;
    private static final int EDITOR_POS = 1;

    private Preview preview = new Preview();
    private Editor editor = new Editor();

    private ViewPager mPager;
    private ModeSwitchAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (mPager.getCurrentItem() == PREVIEW_POS) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mPager.getWindowToken(), 0);
                } else /* if (mPager.getCurrentItem() == EDITOR_POS) */ {
                    EditText edit = (EditText) findViewById(R.id.editor);
                    if (isStickyKeyboardEnabled && edit != null) {
                        edit.requestFocus();
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == PREVIEW_POS) {
                ld.getDrawable(1).setAlpha((int) (0xFF * (1 - positionOffset)));
            } else /* if (position == EDITOR_POS) */ {
                ld.getDrawable(1).setAlpha((int) (0xFF * (positionOffset)));
            }
            rootView.setBackgroundDrawable(ld);
        }

        @Override public void onPageSelected(int position) {}
    };

    // Preferences
    private boolean isStickyKeyboardEnabled;

    // Color transition stuff
    private View rootView;
    private ColorDrawable previewColor;
    private LayerDrawable ld;

    private Note note;

    public Note getNote() {
        return note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);

        note = getIntent().getParcelableExtra(ExtrasNames.NOTE);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ab.setTitle(note.getTitle());
        }
        previewColor = new ColorDrawable(note.getColor());
        ld = new LayerDrawable(new Drawable[]{new ColorDrawable(Color.WHITE), previewColor});
        rootView = getWindow().getDecorView();
        rootView.setBackgroundDrawable(ld);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ModeSwitchAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(mPageChangeListener);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.switch_mode:
                switchMode();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchMode() {
        int current = mPager.getCurrentItem();
        int next = (current == PREVIEW_POS) ? EDITOR_POS : PREVIEW_POS;
        mPager.setCurrentItem(next);
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }

    @Override
    public void onNoteChanged(final String newData) {
        note.setContent(newData);
        Preview preview = (Preview) mPagerAdapter.instantiateItem(mPager, 0);
        preview.updateNotePresentation(newData);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra(ExtrasNames.NOTE, note));
        super.finish();
    }

    private class ModeSwitchAdapter extends FragmentStatePagerAdapter {

        public ModeSwitchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == PREVIEW_POS) {
                return preview;
            } /* else if (position == EDITOR_POS) */
                return editor;
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }
}
