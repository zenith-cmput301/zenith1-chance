package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.models.User;
import com.example.zenithchance.navigation.EntrantNavigationHelper;

public class MainActivity extends AppCompatActivity implements UserProviderInterface {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EntrantNavigationHelper.setupBottomNav(this);
    }

    @Override
    public User getCurrentUser() { return currentUser; }
}
