package com.example.zenithchance.interfaces;

import com.example.zenithchance.models.User;

/**
 * Interface to provide the currently logged-in user to other components (like AllEventsFragment)
 * Used in: EntrantMainActivity, AdminMainActivity
 * @author Kiran
 * @version 1.0
 */
public interface UserProviderInterface {

    /**
     * Returns the currently logged-in user.
     */
    User getCurrentUser();
}
