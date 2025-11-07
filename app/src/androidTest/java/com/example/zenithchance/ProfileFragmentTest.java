package com.example.zenithchance;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zenithchance.fragments.ProfileFragment;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    User testUser;

    @Test
    public void editNameTest(){

        testUser = new User() {
        };
        testUser.setName("testName");
        testUser.setEmail("hi@hi.com");
        Bundle args = new Bundle();
        args.put("user", testUser);

        FragmentScenario<ProfileFragment> scenario =
                FragmentScenario.launchInContainer(ProfileFragment.class, args);
        onView(withId(R.id.editProfileButton)).perform(click());
        SystemClock.sleep(150);
        onView(withId(R.id.editTextUsername)).perform(click());
        SystemClock.sleep(200);
        onView(withId(R.id.editTextUsername)).perform(typeText("newName"));
        SystemClock.sleep(200);
        onView(withId(R.id.confirm_button)).perform(click());
        SystemClock.sleep(200);
        onView(withId(R.id.usernameText)).check((ViewAssertion) withText("nnewName"));
        SystemClock.sleep(150);
    }
}
