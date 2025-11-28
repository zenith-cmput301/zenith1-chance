package com.example.zenithchance;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.zenithchance.activities.OrganizerEventDetailsActivity;
import com.example.zenithchance.fragments.OrganizerCreateEventFragment;
import com.example.zenithchance.fragments.OrganizerEventsFragment;
import com.example.zenithchance.fragments.ProfileFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventDetailsActivityTest {

    private ActivityScenario<OrganizerEventDetailsActivity> launchActivity() {
        Intent intent = new Intent();
        intent.putExtra("eventId", "MockEvent123");
        return ActivityScenario.launch(intent.setClassName(
                "com.example.zenithchance",
                "com.example.zenithchance.activities.OrganizerEventDetailsActivity"));
    }

    @Test
    public void testUIElementsDisplayed() {
        try (ActivityScenario<OrganizerEventDetailsActivity> scenario = launchActivity()) {
            onView(withId(R.id.tvEventName)).check(matches(isDisplayed()));
            onView(withId(R.id.tvEventDate)).check(matches(isDisplayed()));
            onView(withId(R.id.tvEventTime)).check(matches(isDisplayed()));
            onView(withId(R.id.tvLocation)).check(matches(isDisplayed()));
            onView(withId(R.id.tvOrganizer)).check(matches(isDisplayed()));
            onView(withId(R.id.tvAboutDescription)).check(matches(isDisplayed()));

            onView(withId(R.id.btnPeople)).check(matches(isDisplayed()));
            onView(withId(R.id.btnEdit)).check(matches(isDisplayed()));
            onView(withId(R.id.btnMyEvents)).check(matches(isDisplayed()));
            onView(withId(R.id.btnProfile)).check(matches(isDisplayed()));
        }
    }

    // Other test methods...
}
