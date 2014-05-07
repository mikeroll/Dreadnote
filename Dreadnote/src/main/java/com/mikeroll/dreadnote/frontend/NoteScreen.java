package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.mikeroll.dreadnote.R;


public class NoteScreen extends Activity implements Editor.OnNoteChangedListener  {

    private static final int PAGES = 2;
    private static final int PREVIEW_POS = 0;
    private static final int EDITOR_POS = 1;

    private ViewPager mPager;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (mPager.getCurrentItem() == PREVIEW_POS) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mPager.getWindowToken(), 0);
                } else /* if (mPager.getCurrentItem() == EDITOR_POS) */ {
                    EditText edit = (EditText) findViewById(R.id.editor);
                    if (edit != null) {
                        edit.requestFocus();
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override public void onPageSelected(int position) {}
    };
    private ModeSwitchAdapter mPagerAdapter;

    private String note;

    public String getNote() {
        return note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ModeSwitchAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(mPageChangeListener);

        note = getIntent().getStringExtra(ExtrasNames.NOTE_CONTENT);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteChanged(final String newData) {
        note = newData;
        Preview preview = (Preview) mPagerAdapter.instantiateItem(mPager, 0);
        preview.updateNotePresentation(newData);
    }

    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra(ExtrasNames.NOTE_CONTENT, note);
        setResult(RESULT_OK, result);
        super.finish();
    }

    private void switchMode() {
        int current = mPager.getCurrentItem();
        int next = (current == PREVIEW_POS) ? EDITOR_POS : PREVIEW_POS;
        mPager.setCurrentItem(next);
    }

    private class ModeSwitchAdapter extends FragmentStatePagerAdapter {

        private Preview preview = new Preview();
        private Editor editor = new Editor();

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
