package com.mikeroll.dreadnote.frontend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.commonsware.cwac.anddown.AndDown;
import com.mikeroll.dreadnote.R;

public class Preview extends Fragment {

    private WebView webView;

    private String html;

    /** Needed to process links not starting with "http://" */
    private static final String httpRedir = "http://re.dir/";

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
        if (v != null) {
            v.findViewById(R.id.preview).setBackgroundColor(0);
            webView = (WebView) v.findViewById(R.id.preview);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("note:")) {
                        //TODO: well, implement.
                        Toast.makeText(getActivity(), "Note linking not yet implemented.", Toast.LENGTH_SHORT).show();
                    } else {
                        url = url.replace(httpRedir, "http://");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    return true;
                }
            });
        }
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
        html = getCssLink() + converter.markdownToHtml(md);
        Activity a = getActivity();
        if (a != null) a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadDataWithBaseURL(httpRedir, html, "text/html; charset=UTF-8", null, null);
            }
        });
    }

    private static String getCssLink() {
        return "<link rel=\"stylesheet\" href=\"file:///android_asset/note.css\" type=\"text/css\">";
    }
}
