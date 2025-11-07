package com.example.zenithchance;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.User;
import com.example.zenithchance.navigation.AdminNavigationHelper;

public class AdminMainActivity extends AppCompatActivity implements UserProviderInterface {

    private User currentUser;

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        currentUser = UserManager.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        AdminNavigationHelper.setupBottomNav(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });
    }

    public void showBackButton(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
