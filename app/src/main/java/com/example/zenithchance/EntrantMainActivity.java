package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.User;
import com.example.zenithchance.navigation.EntrantNavigationHelper;

/**
 * Main activity for entrant users. Sets up bottom navigation.
 *
 * @author Kiran
 * @version 1.0
 * @see EntrantNavigationHelper
 */
public class EntrantMainActivity extends AppCompatActivity implements EntrantProviderInterface, UserProviderInterface {

    /** The currently logged-in entrant. */
    private Entrant currentEntrant;

    /** The currently logged-in user. */
    private User currentUser;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_main);

        // Fetch current user and entrant info
        currentUser = UserManager.getInstance().getCurrentUser();
        currentEntrant = (Entrant) currentUser;

        // Set up bottom navigation
        EntrantNavigationHelper.setupBottomNav(this, currentEntrant);
    }

    /**
     * Returns the currently logged-in entrant.
     *
     */
    @Override
    public Entrant getCurrentEntrant() {
        return currentEntrant;
    }

    /**
     * Returns the currently logged-in user.
     *
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}
