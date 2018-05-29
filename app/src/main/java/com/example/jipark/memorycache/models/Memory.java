package com.example.jipark.memorycache.models;

/**
 * Created by jipark on 2/4/18.
 */

public class Memory {
    private double latitude;
    private double longitude;
    private String description;
    private String imageLink;
    private Object timeCreated;
    private String id;

    public Memory() {

    }

    public Memory(double latitude, double longitude, String description, String imageLink, Object timeCreated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageLink = imageLink;
        this.timeCreated = timeCreated;
        this.id = "";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Object getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Object timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
