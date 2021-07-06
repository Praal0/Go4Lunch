package com.example.go4lunch.models;

import org.joda.time.DateTime;

import java.util.Date;

public class Message {
    private String message;
    private Date dateCreated;
    private User userSender;
    private User userReceiver;
    private String urlImage;

    public Message(String message, User userSender,User userReceiver , Date dateCreated) {
        this.message = message;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.dateCreated = dateCreated;
    }

    public Message(String message, String urlImage, User userSender,User userReceiver, Date dateCreated) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.dateCreated = dateCreated;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public User getUserReceiver(){return userReceiver;}
    public String getUrlImage() { return urlImage; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public void setUserReceiver(User userReceiver){this.userReceiver = userReceiver;}
}
