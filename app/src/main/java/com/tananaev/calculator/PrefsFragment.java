package com.tananaev.calculator;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {

	public static final String KEY_ONGOING = "pref_ongoing_key";
	public static final String KEY_LOCK_SCREEN = "pref_lock_screen_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
    }

}
