package com.itisdancing.Sokoban;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by David on 28/11/2015.
 */
public class PreferenceSetting extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        EditorPreferenceFragment fragment = new EditorPreferenceFragment();
        transaction.replace(android.R.id.content, fragment);
        transaction.commit();
    }

    public static class EditorPreferenceFragment extends PreferenceFragment {
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.editor_prefs);
        }
    }

    protected void onResume() {
        super.onResume();

    }
}
