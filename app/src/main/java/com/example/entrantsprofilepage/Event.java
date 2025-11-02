package com.example.entrantsprofilepage;

public class Event {
    private String id;
    private String name;
    private String date;
    private String time;
    private String location;
    private String imageUrl;

    public Event(String id, String name, String date, String time, String location, String imageUrl) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
}
