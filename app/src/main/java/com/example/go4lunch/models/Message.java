package com.example.go4lunch.models;

import java.util.Date;

public class Message {
    private String message;
    private Date dateCreated;
    private String idUserSenderIdReceiver;
    private String urlImageUserSender;

    private String urlImage;

    public Message() {}

    public Message(String message, String idUserSender,String urlImageUserSender, Date dateCreated) {
        this.message = message;
        this.idUserSenderIdReceiver = idUserSender;
        this.urlImageUserSender = urlImageUserSender;
        this.dateCreated = dateCreated;
    }

    public Message(String message, String urlImage, String idUserSender,String urlImageUserSender, Date dateCreated) {
        this.message = message;
        this.urlImage = urlImage;
        this.idUserSenderIdReceiver = idUserSender;
        this.urlImageUserSender = urlImageUserSender;
        this.dateCreated = dateCreated;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    public Date getDateCreated() { return dateCreated; }
    public String getUrlImage() { return urlImage; }
    public String getIdUserSenderIdReceiver(){return idUserSenderIdReceiver;}
    public String getUrlImageUserSender(){return urlImageUserSender;}


    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
