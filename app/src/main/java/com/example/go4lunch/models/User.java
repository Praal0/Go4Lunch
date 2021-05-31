package com.example.go4lunch.models;

import androidx.annotation.Nullable;

public class User {
    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private Boolean notification;

    public User() { }

    public User(String uid){
        this.uid = uid;
    }

    public User(String uid, String username, @Nullable String urlPicture, Boolean notification) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.notification = notification;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public Boolean getNotification() {return notification; }


    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }
    public void setNotification(@Nullable Boolean notification) { this.notification = notification; }
}
