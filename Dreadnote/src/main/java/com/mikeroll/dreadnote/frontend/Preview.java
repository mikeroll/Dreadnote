package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.commonsware.cwac.anddown.AndDown;
import com.mikeroll.dreadnote.R;

public class Preview extends Fragment {

    public Preview() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preview, container, false);
        if (v != null) v.findViewById(R.id.preview).setBackgroundColor(0);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NoteScreen ns = (NoteScreen)getActivity();
        if (ns != null) updateNotePresentation(ns.getNote().getContent());
    }

    private AndDown converter = new AndDown();

    public void updateNotePresentation(String md) {
        final String html = converter.markdownToHtml(md);
        Activity a = getActivity();
        if (a != null) a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = (WebView) getView().findViewById(R.id.preview);
                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        });
    }
}
