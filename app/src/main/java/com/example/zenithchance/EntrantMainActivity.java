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
 * Main activity for entrant users.
 * <p>
 * Handles setting up the bottom navigation for the three main tabs:
 * <ul>
 *     <li>All Events</li>
 *     <li>My Events</li>
 *     <li>Profile</li>
 * </ul>
 * Implements {@link EntrantProviderInterface} and {@link UserProviderInterface}
 * to provide the currently logged-in entrant and user.
 * </p>
 *
 * Features:
 * <ul>
 *     <li>Fetches the current entrant and user from {@link UserManager}</li>
 *     <li>Uses {@link EntrantNavigationHelper} to initialize bottom navigation</li>
 * </ul>
 *
 * @author Kiran
 * @version 1.0
 */
public class EntrantMainActivity extends AppCompatActivity implements EntrantProviderInterface, UserProviderInterface {

    /** The currently logged-in entrant. */
    private Entrant currentEntrant;

    /** The currently logged-in user. */
    private User currentUser;

    /**
     * Called when the activity is starting.
     * <p>
     * Retrieves the current entrant and user from {@link UserManager} and sets up
     * the bottom navigation using {@link EntrantNavigationHelper}.
     * </p>
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
     * @return The Entrant object representing the logged-in entrant.
     */
    @Override
    public Entrant getCurrentEntrant() {
        return currentEntrant;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return The User object representing the logged-in user.
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}
