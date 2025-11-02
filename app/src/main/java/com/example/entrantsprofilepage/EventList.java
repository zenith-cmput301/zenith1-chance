package com.example.entrantsprofilepage;

import java.util.ArrayList;
import java.util.List;

/*
This is a Dummy List of Events
*/
public class EventList {

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "Swimming Lessons", "2025-11-01", "10:00 AM", "Community Pool", "https://example.com/swim.jpg"));
        events.add(new Event("2", "Dance Workshop", "2025-11-05", "2:00 PM", "Rec Center Hall", "https://example.com/dance.jpg"));
        events.add(new Event("3", "Piano Lessons", "2025-11-10", "11:00 AM", "Music Room", "https://example.com/piano.jpg"));
        return events;
    }
}
