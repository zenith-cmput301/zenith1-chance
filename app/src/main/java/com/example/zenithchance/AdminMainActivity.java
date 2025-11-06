package com.example.zenithchance;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.R;
import com.example.zenithchance.navigation.AdminNavigationHelper;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        AdminNavigationHelper.setupBottomNav(this);
    }
}