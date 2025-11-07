package com.example.zenithchance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zenithchance.fragments.OrganizerCreateEventFragment;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
import com.example.zenithchance.navigation.OrganizerNavigationHelper;

import java.util.Date;


/**
 * This class represents an Organizers's My Events page.
 *
 * @author Emerson
 * @version 1.0
 * @see OrganizerCreateEventFragment
 */
public class OrganizerMainActivity extends AppCompatActivity {

    Organizer currentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        // get organizer info
        currentOrganizer = (Organizer) UserManager.getInstance().getCurrentUser();

        // Set up the bottom navigation using the helper
        OrganizerNavigationHelper.setupBottomNav(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentOrganizer != null) currentOrganizer.checkAndRunLotteries();
    }

    public Organizer getOrganizer() { return currentOrganizer; }
}

