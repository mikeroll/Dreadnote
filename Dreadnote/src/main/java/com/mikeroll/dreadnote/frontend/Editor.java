package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.mikeroll.dreadnote.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Editor extends Fragment {

    private EditText edit;
    private View toolbar;

    private OnNoteChangeListener mOnNoteChangeListener;

    private static final int TYPE_TIMEOUT = 800;

    public Editor() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editor, container, false);
        //noinspection ConstantConditions
        edit = (EditText) v.findViewById(R.id.editor);
        edit.addTextChangedListener(new OnTypingStoppedListener());
        toolbar = v.findViewById(R.id.editor_toolbar);
        return v;
    }

    @Override
    public void onResume() {
        //noinspection ConstantConditions
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showToolbar = prefs.getBoolean(getString(R.string.pref_toolbar), false);
        toolbar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        String initialData = ((NoteScreen)getActivity()).getNote().getContent();
        edit.append(initialData);
    }
    public interface OnNoteChangeListener {
        public void onNoteContentChanged(final String newData);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnNoteChangeListener = (OnNoteChangeListener) activity;
    }

    private class OnTypingStoppedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) { }

        private ScheduledExecutorService schex = Executors.newSingleThreadScheduledExecutor();
        private ScheduledFuture task;
        @Override
        public void afterTextChanged(final Editable editable) {
            if (task != null) task.cancel(false);
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    mOnNoteChangeListener.onNoteContentChanged(editable.toString());
                }
            };
            task = schex.schedule(update, TYPE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }
}
