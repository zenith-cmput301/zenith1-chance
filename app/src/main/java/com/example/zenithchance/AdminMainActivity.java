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

/**
 * Main activity for admin users.
 *
 * Features:
 * <ul>
 *     <li>Toolbar setup and back button control</li>
 *     <li>Fragment navigation using {@link AdminNavigationHelper}</li>
 *     <li>Custom back press handling for fragment back stack</li>
 * </ul>
 *
 * @author Kiran
 * @version 1.0
 * @see UserProviderInterface
 */
public class AdminMainActivity extends AppCompatActivity implements UserProviderInterface {

    /** The currently logged-in user. */
    private User currentUser;

    /**
     * Returns the currently logged-in user.
     *
     * @return currentUser The User object representing the admin.
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Get the current logged-in user
        currentUser = UserManager.getInstance().getCurrentUser();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Setup bottom navigation for admin
        AdminNavigationHelper.setupBottomNav(this);

        // Custom back button handling for fragment back stack
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

    /**
     * Shows or hides the back button in the toolbar.
     *
     * @param show true to display the back button, false to hide it
     */
    public void showBackButton(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

    /**
     * Handles toolbar navigation up click.
     *
     * @return true if handled
     */
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
