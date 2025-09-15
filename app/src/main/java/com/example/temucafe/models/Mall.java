package com.example.temucafe.models;

import com.google.firebase.firestore.Exclude;

public class Mall {
    @Exclude
    private String documentId;
    private String name;
    private String imageUrl;

    public Mall() {
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}