package com.mikeroll.dreadnote.frontend;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.commonsware.cwac.anddown.AndDown;
import com.mikeroll.dreadnote.R;

import java.util.NoSuchElementException;

public class Preview extends Fragment {

    private WebView webView;

    private String html;
    private AndDown converter = new AndDown();

    /** Needed to process links not starting with "http://" */
    private static final String HTTP_REDIR = "http://re.dir/";
    private static final String HTTP = "http://";

    private static final String NOTE_PREFIX = "note:";

    private static final String CSS = "<link rel=\"stylesheet\" href=\"file:///android_asset/note.css\" type=\"text/css\">";

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
        //noinspection ConstantConditions
        webView = (WebView) v.findViewById(R.id.preview);
        webView.setBackgroundColor(0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new NoteViewClient());
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NoteScreen ns = (NoteScreen)getActivity();
        if (ns != null) updateNotePresentation(ns.getNote().getContent());
    }

    public void updateNotePresentation(String md) {
        html = CSS + converter.markdownToHtml(md);
        //noinspection ConstantConditions
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadDataWithBaseURL(HTTP_REDIR, html, "text/html", "UTF-8", null);
            }
        });
    }

    private class NoteViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(NOTE_PREFIX)) {
                try {
                    jumpToNote(url);
                } catch (NoSuchElementException e) {
                    //noinspection ConstantConditions
                    Toast.makeText(Preview.this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                url = url.replace(HTTP_REDIR, HTTP);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
            return true;
        }

        private void jumpToNote(final String url) {
            long id;
            try {
                id = Long.parseLong(url.substring(NOTE_PREFIX.length()));
            } catch (NumberFormatException e) {
                throw new NoSuchElementException(getString(R.string.error_link_invalid));
            }
            //noinspection ConstantConditions
            if (((NoteScreen) getActivity()).getDBClient().exists(id)) {
                Intent intent = new Intent(getActivity(), NoteScreen.class);
                intent.putExtra(ExtrasNames.NOTE_ID, id);
                startActivity(intent);
            } else {
                throw new NoSuchElementException(getString(R.string.error_link_not_found));
            }
        }
    }
}
