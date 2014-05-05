package com.mikeroll.dreadnote.frontend;

import android.support.v4.app.Fragment;
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
        assert v != null;
        v.findViewById(R.id.preview).setBackgroundColor(0);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String initialData = ((NoteScreen)getActivity()).getNote();
        updateNotePresentation(initialData);
    }

    private AndDown converter = new AndDown();

    public void updateNotePresentation(String md) {
        final String html = converter.markdownToHtml(md);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = (WebView) getView().findViewById(R.id.preview);
                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        });
    }
}
