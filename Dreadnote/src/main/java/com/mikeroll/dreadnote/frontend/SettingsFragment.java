package com.mikeroll.dreadnote.frontend;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.mikeroll.dreadnote.R;


public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
