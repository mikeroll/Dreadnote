package com.mikeroll.dreadnote.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class NoteScreen extends FragmentActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ModeSwitchAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // TODO: setNoteColor(0xFFFFF8DC); //temporary!
    }

//    TODO
//    public void setNoteColor(int color) {
//        this.getWindow().getDecorView().setBackgroundColor(color);
//        assert getActionBar() != null;
//        getActionBar().setBackgroundDrawable(new ColorDrawable(0)); //transparent
//    }

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
        int current = mPager.getCurrentItem();
        int next = (current == 0) ? 1 : 0;
        mPager.setCurrentItem(next);
    }

    private class ModeSwitchAdapter extends FragmentStatePagerAdapter {

        private static final int PAGES = 2;

        public ModeSwitchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new Preview();
            else if (position == 1)
                return new Editor();
            return null;
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }
}
