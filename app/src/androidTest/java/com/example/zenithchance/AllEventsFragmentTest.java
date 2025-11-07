package com.example.zenithchance;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.zenithchance.fragments.AllEventsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
/**
 * Instrumentation tests for {@link AllEventsFragment}.
 */
@RunWith(AndroidJUnit4.class)
public class AllEventsFragmentTest {

    /**
     * Verifies that the fragment can be launched successfully.
     */
    @Test
    public void testFragmentLaunch() {
        FragmentScenario<AllEventsFragment> scenario =
                FragmentScenario.launchInContainer(AllEventsFragment.class, null, R.style.AppTheme);

        scenario.onFragment(fragment -> {
            // Verify fragment view is created
            assertNotNull(fragment.getView());
        });
    }
}

