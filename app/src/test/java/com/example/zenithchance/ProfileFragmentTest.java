package com.example.zenithchance;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for ProfileFragmentTest
 */

// TODO: Fix this test
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {


    @Test
    public void editNameTest(){
        onView(withId(R.id.editProfileButton)).perform(click());
        SystemClock.sleep(150);
        onView(withId(R.id.editTextUsername)).perform(click());
        SystemClock.sleep(150);
        onView(withId(R.id.editTextUsername)).perform(typeText("newName"));
        SystemClock.sleep(150);
        onView(withId(R.id.confirm_button)).perform(click());
        SystemClock.sleep(150);
        onView(withId(R.id.usernameText)).check((ViewAssertion) withText("newName"));
        SystemClock.sleep(150);
    }
}
