package com.tananaev.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new PrefsFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((MainApplication) getApplication()).showNotification();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PrefsFragment.KEY_LOCK_SCREEN.equals(key) ||
                PrefsFragment.KEY_ONGOING.equals(key)) {
            ((MainApplication) getApplication()).showNotification();
        }
    }

}
