package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.mikeroll.dreadnote.R;
import com.mikeroll.dreadnote.storage.DBClient;
import com.mikeroll.dreadnote.storage.DBHelper;


public class Dashboard extends Activity {

    private DBClient mDBClient;
    private ListView noteList;
    private ResourceCursorAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFirstRunConfig();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_dashboard);

        noteList = (ListView) findViewById(R.id.note_list);
        noteListAdapter  = new NoteAdapter(this);
        noteList.setAdapter(noteListAdapter);
        noteList.setOnItemClickListener(new OnNoteClickListener());
        noteList.setMultiChoiceModeListener(new NotePickListener());

        mDBClient = new DBClient(DBHelper.connect(getApplicationContext()));
    }

    private void performFirstRunConfig() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = prefs.getBoolean("isfirstrun", true);

        if (isFirstRun) {
            prefs.edit().putBoolean("isfirstrun", false).commit();
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UpdateListTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_note:
                openNote(-1);
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        DBHelper.disconnect();
        super.onDestroy();
    }

    private void openSettings() {
        startActivity(new Intent(this, Settings.class));
    }

    private void openNote(long id) {
        Intent intent = new Intent(Dashboard.this, NoteScreen.class);
        intent.putExtra(ExtrasNames.NOTE_ID, id);
        startActivity(intent);
    }

    @SuppressWarnings("ConstantConditions")
    private void deleteSelected() {
        SparseBooleanArray selected = noteList.getCheckedItemPositions();
        if (selected != null) {
            Long[] ids = new Long[selected.size()];
            int p = 0;
            for (int i = 0; i < noteListAdapter.getCount(); i++) {
                if (selected.get(i)) {
                    ids[p++] = ((Cursor)noteListAdapter.getItem(i)).getLong(0);
                }
            }
            new DeleteNotesTask().execute(ids);
            new UpdateListTask().execute();
        }
    }

    private class DeleteNotesTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... ids) {
            mDBClient.deleteNotes(ids);
            return null;
        }
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDBClient.selectAll();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            noteListAdapter.changeCursor(cursor);
            noteListAdapter.notifyDataSetChanged();
            setProgressBarIndeterminateVisibility(false);
        }
    }

    private class NoteAdapter extends ResourceCursorAdapter {

        public NoteAdapter(Context context) {
            super(context, R.layout.list_item, null, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            int color = cursor.getInt(2);
            ((TextView) view.findViewById(R.id.list_item_id)).setText("#" + Long.toString(id));
            TextView textView = (TextView) view.findViewById(R.id.list_item_title);
            textView.setText(title);
            textView.setSelected(true);
            view.findViewById(R.id.list_item_colorbox).setBackgroundColor(color);
            view.setTag(id);
        }
    }

    private class OnNoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            long id = (Long)view.getTag();
            openNote(id);
        }
    }

    private class NotePickListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
            actionMode.setTitle(String.format("%d %s", noteList.getCheckedItemCount(), getString(R.string.selected)));
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.notelist_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    deleteSelected();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {}
    }
}
