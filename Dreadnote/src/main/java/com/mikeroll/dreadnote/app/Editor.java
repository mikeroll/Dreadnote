package com.mikeroll.dreadnote.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Editor extends Fragment {

    public Editor() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editor, container, false);
        assert v != null;
        EditText edit = (EditText) v.findViewById(R.id.editor);
        edit.addTextChangedListener(typeListener);

        return v;
    }

    private static final int TYPE_TIMEOUT = 800;

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            //TODO: implement;
        }
    };

    private final TextWatcher typeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) { }

        private ScheduledExecutorService schex = Executors.newSingleThreadScheduledExecutor();
        private ScheduledFuture task;
        @Override
        public void afterTextChanged(final Editable editable) {
            if (task != null) task.cancel(false);
            task = schex.schedule(update, TYPE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    };
}
