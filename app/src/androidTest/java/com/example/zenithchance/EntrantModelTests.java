package com.example.zenithchance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.zenithchance.models.Entrant;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the Entrant Model methods.
 */
public class EntrantModelTests {

    @Test
    public void constructorSetsTypeEntrant() {
        Entrant e = new Entrant();
        assertEquals("entrant", e.getType());
    }

    @Test
    public void isInWaitingListTrueWhenPresent() {
        Entrant e = new Entrant();
        e.getOnWaiting().add("E1");

        assertTrue(e.isInWaitingList("E1"));
        assertFalse(e.isInWaitingList("E2"));
    }

    @Test
    public void isInInvitedAcceptedDeclined() {
        Entrant e = new Entrant();
        e.getOnInvite().add("I1");
        e.getOnAccepted().add("A1");
        e.getOnDeclined().add("D1");

        assertTrue(e.isInInvitedList("I1"));
        assertFalse(e.isInInvitedList("X"));

        assertTrue(e.isInAcceptedList("A1"));
        assertFalse(e.isInAcceptedList("X"));

        assertTrue(e.isInDeclinedList("D1"));
        assertFalse(e.isInDeclinedList("X"));
    }

    @Test
    public void isInAnyListCoversAllLists() {
        Entrant e = new Entrant();
        e.getOnWaiting().add("W1");
        e.getOnInvite().add("I1");
        e.getOnAccepted().add("A1");
        e.getOnDeclined().add("D1");

        assertTrue(e.isInAnyList("W1"));
        assertTrue(e.isInAnyList("I1"));
        assertTrue(e.isInAnyList("A1"));
        assertTrue(e.isInAnyList("D1"));
        assertFalse(e.isInAnyList("NOPE"));
    }

    @Test
    public void listsAreIndependent() {
        Entrant e = new Entrant();
        e.getOnWaiting().add("W1");

        assertTrue(e.isInWaitingList("W1"));
        assertFalse(e.isInInvitedList("W1"));
        assertFalse(e.isInAcceptedList("W1"));
        assertFalse(e.isInDeclinedList("W1"));
    }

    @Test
    public void gettersReturnLiveLists() {
        Entrant e = new Entrant();
        ArrayList<String> waiting = e.getOnWaiting();
        waiting.add("X");

        assertTrue(e.isInWaitingList("X"));
    }

}
