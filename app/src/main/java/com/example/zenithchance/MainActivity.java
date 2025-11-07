package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.navigation.EntrantNavigationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EntrantNavigationHelper.setupBottomNav(this, null);
    }
}
