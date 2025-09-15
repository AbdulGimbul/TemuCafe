// app/src/main/java/com/example/temucafe/models/CoffeeShop.java
package com.example.temucafe.models;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class CoffeeShop {

    // At the top with other variable declarations
    @Exclude
    private String originType; // e.g., "industrial", "franchise"
    @Exclude
    private String mallId; // Only used if it's from a mall

    // In the Getters section
    @Exclude
    public String getOriginType() { return originType; }
    @Exclude
    public String getMallId() { return mallId; }

    // In the Setters section
    @Exclude
    public void setOriginType(String originType) { this.originType = originType; }
    @Exclude
    public void setMallId(String mallId) { this.mallId = mallId; }

    // IMPORTANT: These variable names must EXACTLY match the keys in your Firestore documents.
    private String name;
    private String location;
    private String address;
    private String mapLink;
    private String hours;
    private String greeting;
    private String bannerImageUrl;
    private String logoIconUrl;
    private List<String> actions;
    private List<String> accessibility;
    private List<String> serviceOptions;
    private List<String> parking;
    private String menuLink;

    // We use @Exclude so Firestore doesn't try to save this field back to the database.
    // It's only for use within the app.
    @Exclude
    private String documentId;

    // A public, no-argument constructor is required for Firestore's automatic data mapping.
    public CoffeeShop() {
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getLogoIconUrl() {
        return logoIconUrl;
    }

    public void setLogoIconUrl(String logoIconUrl) {
        this.logoIconUrl = logoIconUrl;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(List<String> accessibility) {
        this.accessibility = accessibility;
    }

    public List<String> getServiceOptions() {
        return serviceOptions;
    }

    public void setServiceOptions(List<String> serviceOptions) {
        this.serviceOptions = serviceOptions;
    }

    public List<String> getParking() {
        return parking;
    }

    public void setParking(List<String> parking) {
        this.parking = parking;
    }

    public String getMenuLink() {
        return menuLink;
    }

    public void setMenuLink(String menuLink) {
        this.menuLink = menuLink;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}