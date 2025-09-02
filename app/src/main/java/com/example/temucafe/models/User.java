package com.example.temucafe.models;

public class User {
    public String username;
    public String email;

    // Konstruktor kosong (wajib untuk Firebase)
    public User() {
    }

    // Konstruktor lengkap
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getter dan Setter (opsional tapi baik untuk praktik OOP)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
