package com.ivkil.sunshine.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ivkil.sunshine.R;
import com.ivkil.sunshine.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

}
