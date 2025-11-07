package com.example.zenithchance;
import static org.junit.Assert.*;

import com.example.zenithchance.models.Event;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class EventsModelTests {

    @Test
    public void settersUpdateSimpleFields() {
        Event e = new Event();

        e.setName("Hackathon");
        e.setLocation("Campus Hall");
        e.setStatus("accepted");
        e.setOrganizer("CS Club");
        e.setDescription("24h coding");
        e.setMaxEntrants(200);
        e.setDocId("EVENT_123");

        assertEquals("Hackathon", e.getName());
        assertEquals("Campus Hall", e.getLocation());
        assertEquals("accepted", e.getStatus());
        assertEquals("CS Club", e.getOrganizer());
        assertEquals("24h coding", e.getDescription());
        assertEquals(Integer.valueOf(200), e.getMaxEntrants());
        assertEquals("EVENT_123", e.getDocId());
    }

    @Test
    public void docIdSetterGetterWorks() {
        Event e = new Event();
        e.setDocId("DOC_42");
        assertEquals("DOC_42", e.getDocId());
    }

    import static org.junit.Assert.*;
import org.junit.Test;

    public class EventListMethodsTests {

        @Test
        public void addWaitingListAddsUidOnce() {
            Event e = new Event();

            e.addWaitingList("U1");
            e.addWaitingList("U1"); // duplicate ignored

            assertEquals(1, e.getOnWaitingList().size());
            assertTrue(e.getOnWaitingList().contains("U1"));
        }

        @Test
        public void removeFromWaitingListRemovesUid() {
            Event e = new Event();
            e.addWaitingList("U1");

            e.removeFromWaitingList("U1");

            assertFalse(e.getOnWaitingList().contains("U1"));
            assertTrue(e.getOnWaitingList().isEmpty());
        }

        @Test
        public void addInvitedListAddsUidOnce() {
            Event e = new Event();

            e.addInvitedList("U2");
            e.addInvitedList("U2");

            // This list is private, but if you add a getter similar to getOnWaitingList(),
            // use that to check directly. Assuming same naming:
            // e.getInvitedList()
            assertEquals(1, e.getInvitedList().size());
            assertTrue(e.getInvitedList().contains("U2"));
        }

        @Test
        public void removeFromInvitedListRemovesUid() {
            Event e = new Event();
            e.addInvitedList("U3");

            e.removeFromInvitedList("U3");

            assertTrue(e.getInvitedList().isEmpty());
        }

        @Test
        public void addAcceptedListAddsUidOnce() {
            Event e = new Event();

            e.addAcceptedList("U4");
            e.addAcceptedList("U4");

            assertEquals(1, e.getAcceptedList().size());
            assertTrue(e.getAcceptedList().contains("U4"));
        }

        @Test
        public void addDeclinedListAddsUidOnce() {
            Event e = new Event();

            e.addDeclinedList("U5");
            e.addDeclinedList("U5");

            assertEquals(1, e.getDeclinedList().size());
            assertTrue(e.getDeclinedList().contains("U5"));
        }
    }

}
