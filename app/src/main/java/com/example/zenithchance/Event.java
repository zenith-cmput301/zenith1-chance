package com.example.zenithchance;

import java.util.Date;

public class Event {
    public Date date;
    public String name;
    public String location;
    public String status;

    public Event() {} // needed for Firestore

    public Event(Date date, String name, String location, String status) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
    }

}
