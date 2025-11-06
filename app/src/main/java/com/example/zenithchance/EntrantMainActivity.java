package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.navigation.EntrantNavigationHelper;
// SIGN-IN Page redirects to EntrantMainActivity: if user type = entrant

public class EntrantMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_main);

        // Set up the bottom navigation using the helper
        EntrantNavigationHelper.setupBottomNav(this);
    }
}
