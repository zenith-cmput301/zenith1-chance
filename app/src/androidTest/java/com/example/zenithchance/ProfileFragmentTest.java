package com.example.zenithchance;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zenithchance.fragments.ProfileFragment;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    @Test
    public void testChangeNameAndEmailEntrant() {
        // Create a test user and inject it into UserManager
        User entrant = new Entrant();
        entrant.setName("Jane");
        entrant.setUserId("jane123");
        entrant.setType("entrant");
        UserManager.getInstance().setCurrentUser(entrant);
        // Launch the fragment in isolation
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            // Check initial values
            TextView usernameDisplay = fragment.getView().findViewById(R.id.usernameText);
            TextView emailDisplay = fragment.getView().findViewById(R.id.emailText);
            EditText editUsername = fragment.getView().findViewById(R.id.editTextUsername);
            EditText editEmail = fragment.getView().findViewById(R.id.editTextEmail);

            assertEquals("Jane", usernameDisplay.getText().toString());
            assertEquals("", emailDisplay.getText().toString());

            // Call fragment's functions directly
            fragment.changeName("NewName");
            fragment.changeEmail("new@email.com");

            // Verify changes
            assertEquals("NewName", usernameDisplay.getText().toString());
            assertEquals("new@email.com", emailDisplay.getText().toString());

            // Call with invalid email
            fragment.changeEmail("bademail");
            assertTrue(editEmail.getError() != null);

            // Call with empty name/email - should revert to original
            fragment.changeName("");
            assertEquals("NewName", usernameDisplay.getText().toString()); // unchanged
            fragment.changeEmail("");
            assertEquals("new@email.com", emailDisplay.getText().toString()); // unchanged
        });
    }

    @Test
    public void testChangeNameAndEmailOrganizer() {
        // Create a test user and inject it into UserManager
        User organizer = new Organizer();
        organizer.setName("Jane");
        organizer.setUserId("jane123");
        organizer.setType("organizer");
        UserManager.getInstance().setCurrentUser(organizer); // assume you have a setter for testing
        // Launch the fragment in isolation
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            // Check initial values
            TextView usernameDisplay = fragment.getView().findViewById(R.id.usernameText);
            TextView emailDisplay = fragment.getView().findViewById(R.id.emailText);
            EditText editUsername = fragment.getView().findViewById(R.id.editTextUsername);
            EditText editEmail = fragment.getView().findViewById(R.id.editTextEmail);

            assertEquals("Jane", usernameDisplay.getText().toString());
            assertEquals("", emailDisplay.getText().toString());

            // Call fragment's functions directly
            fragment.changeName("NewName");
            fragment.changeEmail("new@email.com");

            // Verify changes
            assertEquals("NewName", usernameDisplay.getText().toString());
            assertEquals("new@email.com", emailDisplay.getText().toString());

            // Call with invalid email
            fragment.changeEmail("bademail");
            assertTrue(editEmail.getError() != null);  // Error message should be set

            // Call with empty name/email - should revert to original
            fragment.changeName("");
            assertEquals("NewName", usernameDisplay.getText().toString()); // unchanged
            fragment.changeEmail("");
            assertEquals("new@email.com", emailDisplay.getText().toString()); // unchanged
        });
    }

    @Test
    public void testChangeNameAndEmailAdmin() {
        // Create a test user and inject it into UserManager
        User admin = new Admin();
        admin.setName("Jane");
        admin.setUserId("jane123");
        admin.setType("admin");
        UserManager.getInstance().setCurrentUser(admin); // assume you have a setter for testing
        // Launch the fragment in isolation
        FragmentScenario<ProfileFragment> scenario = FragmentScenario.launchInContainer(ProfileFragment.class);

        scenario.onFragment(fragment -> {
            // Check initial values
            TextView usernameDisplay = fragment.getView().findViewById(R.id.usernameText);
            TextView emailDisplay = fragment.getView().findViewById(R.id.emailText);
            EditText editUsername = fragment.getView().findViewById(R.id.editTextUsername);
            EditText editEmail = fragment.getView().findViewById(R.id.editTextEmail);

            assertEquals("Jane", usernameDisplay.getText().toString());
            assertEquals("", emailDisplay.getText().toString());

            // Call fragment's functions directly
            fragment.changeName("NewName");
            fragment.changeEmail("new@email.com");

            // Verify changes
            assertEquals("NewName", usernameDisplay.getText().toString());
            assertEquals("new@email.com", emailDisplay.getText().toString());

            // Call with invalid email
            fragment.changeEmail("bademail");
            assertTrue(editEmail.getError() != null);  // Error message should be set

            // Call with empty name/email - should revert to original
            fragment.changeName("");
            assertEquals("NewName", usernameDisplay.getText().toString()); // unchanged
            fragment.changeEmail("");
            assertEquals("new@email.com", emailDisplay.getText().toString()); // unchanged
        });
    }
    @Test
    public void testButtons() {
        User entrant = new Entrant();
        entrant.setName("Jane");
        entrant.setUserId("jane123");
        entrant.setType("entrant");
        UserManager.getInstance().setCurrentUser(entrant);
        // Launch fragment
        FragmentScenario<ProfileFragment> scenario =
                FragmentScenario.launchInContainer(ProfileFragment.class);

        // 1Perform UI actions with Espresso (outside onFragment) OR ELSE it breaks
        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(typeText("New"));
        onView(withId(R.id.confirm_button)).perform(click());

        // Check the fragment state directly
        scenario.onFragment(fragment -> {
            // Access TextView directly
            TextView usernameDisplay = fragment.getView().findViewById(R.id.usernameText);
            assertEquals("JaneNew", usernameDisplay.getText().toString());
        });
    }
}
