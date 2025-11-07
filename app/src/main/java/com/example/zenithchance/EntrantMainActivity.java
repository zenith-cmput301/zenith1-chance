package com.example.zenithchance;
import com.example.zenithchance.interfaces.EntrantProviderInterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
// SIGN-IN Page redirects to EntrantMainActivity: if user type = entrant

public class EntrantMainActivity extends AppCompatActivity implements EntrantProviderInterface {
    private Entrant currentEntrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_main);

        // get entrant info
        currentEntrant = (Entrant) UserManager.getInstance().getCurrentUser();

        // Set up the bottom navigation using the helper
        EntrantNavigationHelper.setupBottomNav(this, currentEntrant);
    }

    @Override
    public Entrant getCurrentEntrant() {
        return currentEntrant;
    }
}
