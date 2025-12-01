package com.example.zenithchance.models;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * The representative class for all Events.
 * All information pertaining to an event should be found here.
 *
 * @author Percy, Sabrina
 * @version 1.1
 */

public class Event implements Serializable {
    private Date date;
    private String name;
    private String organizer; // name of organizer, not document id
    private String status;
    private String description;
    private Boolean geolocation_required;

    private String location; // readable location String (e.g. "University of Alberta")

    private Double latitude;   // latitude and longitude for location geopoint
    private Double longitude;  //

    private Date registration_date;
    private Date finalDeadline;
    private Integer max_entrants;
    private String imageUrl;
    private boolean lotteryRan = false;
    private boolean needRedraw = false;
    private ArrayList<String> waitingList = new ArrayList<String>();
    private ArrayList<String> invitedList = new ArrayList<String>();
    private ArrayList<String> acceptedList = new ArrayList<String>();
    private ArrayList<String> declinedList = new ArrayList<String>();

    private String docId;

    /**
     * Empty constructor for class Event.
     *
     * @return an instance of the Event object
     */
    public Event() {} // needed for Firestore

    /**
     * Constructor to create a new Event instance
     *
     * @param date                  date of event
     * @param name                  name of event
     * @param location              location of event
     * @param status                entrant's status on event (waiting, chosen, accepted, declined)
     * @param organizer             organizer of the event
     * @param description           event description
     * @param geolocation_required  boolean representing if geolocation is toggled
     * @param registration_date     date in which to close registration (lottery is drawn)
     * @param finalDeadline         date of finalizing attendees
     * @param max_entrants          the maximum number of entrants allowed to attend the event
     * @return an instance of the Event object
     */
    public Event(Date date, String name, String location, String status, String organizer, String description, Boolean geolocation_required, Date registration_date, Date finalDeadline, Integer max_entrants) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
        this.organizer = organizer;
        this.description = description;
        this.geolocation_required = geolocation_required;
        this.registration_date = registration_date;
        this.finalDeadline = finalDeadline;
        this.max_entrants = max_entrants;
    }

    /**
     * Adders
     * @param uid User id
     */
    public void addWaitingList(String uid) {
        if (!waitingList.contains(uid)) waitingList.add(uid);
    }

    public void addInvitedList(String uid) {
        if (!invitedList.contains(uid)) invitedList.add(uid);
    }

    public void addAcceptedList(String uid) {
        if (!acceptedList.contains(uid)) acceptedList.add(uid);
    }

    public void addDeclinedList(String uid) {
        if (!declinedList.contains(uid)) declinedList.add(uid);
    }

    /**
     * Removers
     * @param uid User id
     */

    public void removeFromWaitingList(String uid) {
        this.waitingList.remove(uid);
    }
    public void removeFromInvitedList(String uid) {
        this.invitedList.remove(uid);
    }
    public void removeFromAcceptedList(String uid) {
        this.acceptedList.remove(uid);
    }

    /**
     * Time checks
     */

    public boolean isPast(Date now) {
        return date != null && date.before(now);
    }

    public boolean isUpcoming(Date now) {
        return date == null || !date.before(now);
    }

    /**
     * Getters
     */

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }


    public String getOrganizer() {
        return organizer;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getGeolocationRequired() {
        return geolocation_required;
    }

    public Date getRegistrationDate() {
        return registration_date;
    }

    public Date getFinalDeadline() {
        return finalDeadline;
    }

    public Integer getMaxEntrants() {
        return max_entrants;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isLotteryRan() {
        return lotteryRan;
    }

    public boolean isNeedRedraw() {
        return needRedraw;
    }

    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    public ArrayList<String> getInvitedList() {
        return invitedList;
    }

    public ArrayList<String> getAcceptedList() {
        return acceptedList;
    }

    public ArrayList<String> getDeclinedList() {
        return declinedList;
    }

    public String getDocId() {
        return docId;
    }

    public String getLocation() {
        return location;
    }

    public GeoPoint getLocationPoint() {
        if (latitude != null && longitude != null) {
            return new GeoPoint(latitude, longitude);
        }
        return null;
    }

    // Also add individual getters/setters for Firestore
    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }

    /**
     * Setters
     */

    // Setter that accepts GeoPoint
    public void setLocationPoint(GeoPoint geoPoint) {
        if (geoPoint != null) {
            this.latitude = geoPoint.getLatitude();
            this.longitude = geoPoint.getLongitude();
        } else {
            this.latitude = null;
            this.longitude = null;
        }
    }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public void setLocation(String location) {
        this.location = location;
    }



    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGeolocationRequired(Boolean geolocation_required) {
        this.geolocation_required = geolocation_required;
    }

    public void setRegistrationDate(Date registration_date) {
        this.registration_date = registration_date;
    }

    public void setFinalDeadline(Date finalDeadline) {
        this.finalDeadline = finalDeadline;
    }

    public void setMaxEntrants(Integer max_entrants) {
        this.max_entrants = max_entrants;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLotteryRan(boolean lotteryRan) {
        this.lotteryRan = lotteryRan;
    }

    public void setNeedRedraw(boolean needRedraw) {
        this.needRedraw = needRedraw;
    }

    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    public void setInvitedList(ArrayList<String> invitedList) {
        this.invitedList = invitedList;
    }

    public void setAcceptedList(ArrayList<String> acceptedList) {
        this.acceptedList = acceptedList;
    }

    public void setDeclinedList(ArrayList<String> declinedList) {
        this.declinedList = declinedList;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
