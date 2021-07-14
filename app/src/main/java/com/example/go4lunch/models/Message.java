package com.example.go4lunch.models;

import org.joda.time.DateTime;

import java.util.Date;

public class Message {
    private String message;
    private Date dateCreated;
    private String idUserSender;
    private String urlImageUserSender;
    private String idUserReceiver;
    private String urlImage;

    public Message() {}

    public Message(String message, String idUserSender,String urlImageUserSender,String idUserReceiver, Date dateCreated) {
        this.message = message;
        this.idUserSender = idUserSender;
        this.urlImageUserSender = urlImageUserSender;
        this.idUserReceiver = idUserReceiver;
        this.dateCreated = dateCreated;
    }

    public Message(String message, String urlImage, String idUserSender,String urlImageUserSender,String idUserReceiver, Date dateCreated) {
        this.message = message;
        this.urlImage = urlImage;
        this.idUserSender = idUserSender;
        this.urlImageUserSender = urlImageUserSender;
        this.idUserReceiver = idUserReceiver;
        this.dateCreated = dateCreated;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    public Date getDateCreated() { return dateCreated; }
    public String getUrlImage() { return urlImage; }
    public String getIdUserSender(){return idUserSender;}
    public String getUrlImageUserSender(){return urlImageUserSender;}
    public String getIdUserReceiver(){return idUserReceiver;}


    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
