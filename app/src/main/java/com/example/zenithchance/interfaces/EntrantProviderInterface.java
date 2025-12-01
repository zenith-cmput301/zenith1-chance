package com.example.zenithchance.interfaces;

import com.example.zenithchance.models.Entrant;

/**
 * Interface to provide the currently logged-in Entrant to other components
 * Used in: EntrantMainActivity
 *
 */
public interface EntrantProviderInterface {
    Entrant getCurrentEntrant();
}