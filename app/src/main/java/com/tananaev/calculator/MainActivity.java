package com.tananaev.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(MainActivity.class.getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
        //finish();
    }

    @Override
    protected void onStart() {
        Log.v(MainActivity.class.getSimpleName(), "onStart()");
        super.onStart();
        ((MainApplication) getApplication()).showNotification();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        Log.v(MainActivity.class.getSimpleName(), "onStop()");
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(MainActivity.class.getSimpleName(), "onSharedPreferenceChanged("+key+")");
        if (PrefsFragment.KEY_LOCK_SCREEN.equals(key) ||
                PrefsFragment.KEY_ONGOING.equals(key)) {
            ((MainApplication) getApplication()).showNotification();
        }
    }

}
