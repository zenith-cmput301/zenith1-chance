//package com.example.zenithchance;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//import android.os.SystemClock;
//
//import androidx.fragment.app.FragmentManager;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//
//import com.example.zenithchance.activities.EntrantEventsActivity;
//import com.example.zenithchance.fragments.EntrantEventListFragment;
//import com.example.zenithchance.models.Event;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
///**
// * UI tests for EntrantEventsActivity
// */
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class EntrantEventsActivityTest {
//
//    @Rule
//    public ActivityScenarioRule<EntrantEventsActivity> scenario =
//            new ActivityScenarioRule<>(EntrantEventsActivity.class);
//
//    /**
//     * Helper: injects a sample list of Events directly into the EntrantEventListFragment.
//     * Avoids Firebase, as team does not have a unified database yet
//     */
//    private void injectSampleEvents() {
//        scenario.getScenario().onActivity(activity -> {
//            FragmentManager fm = activity.getSupportFragmentManager();
//            fm.executePendingTransactions();
//            EntrantEventListFragment frag =
//                    (EntrantEventListFragment) fm.findFragmentByTag("entrant_event_list");
//
//            long now = System.currentTimeMillis();
//            Date future = new Date(now + TimeUnit.DAYS.toMillis(7));
//            Date past   = new Date(now - TimeUnit.DAYS.toMillis(7));
//
//            ArrayList<Event> sample = new ArrayList<>();
////            sample.add(new Event(future, "Future Hackathon", "UofA", "Waiting", "UofA", "lorem ipsum"));
////            sample.add(new Event(past,   "Past Contest",     "ETLC", "Accepted", "ETLC club", "lorem ipsum"));
//
//            // Push data into the fragment (the fragment internally filters by date).
//            frag.setEvents(sample);
//        });
//
//        // Give RecyclerView/adapter a moment to render
//        SystemClock.sleep(200);
//    }
//
//    @Test
//    public void list_displaysWithInjectedEvents() {
//        // Sanity: the container & recycler should be on screen.
//        onView(withId(R.id.eventsFragmentContainer)).check(matches(isDisplayed()));
//        onView(withId(R.id.recycler_events)).check(matches(isDisplayed()));
//
//        injectSampleEvents();
//
//        // Verify list item text shows up
//        onView(withText("Future Hackathon")).check(matches(isDisplayed()));
//        onView(withText("Past Contest")).check(matches(isDisplayed()));
//        onView(withText("UofA")).check(matches(isDisplayed()));
//        onView(withText("ETLC")).check(matches(isDisplayed()));
//        onView(withText("Waiting")).check(matches(isDisplayed()));
//        onView(withText("Accepted")).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void filterButtons_toggleUpcomingAndPast() {
//        injectSampleEvents();
//
//        // Default screen should have the heading and buttons.
//        onView(withId(R.id.my_events_heading)).check(matches(isDisplayed()));
//        onView(withId(R.id.upcoming_events)).check(matches(isDisplayed()));
//        onView(withId(R.id.past_events)).check(matches(isDisplayed()));
//
//        // tap "Upcoming", only future event should remain visible
//        onView(withId(R.id.upcoming_events)).perform(click());
//        SystemClock.sleep(150);
//        onView(withText("Future Hackathon")).check(matches(isDisplayed()));
//        onView(withText("Past Contest")).check(doesNotExist());
//
//        // tap "Past", only past event should remain visible
//        onView(withId(R.id.past_events)).perform(click());
//        SystemClock.sleep(150);
//        onView(withText("Past Contest")).check(matches(isDisplayed()));
//        onView(withText("Future Hackathon")).check(doesNotExist());
//    }
//}
//
