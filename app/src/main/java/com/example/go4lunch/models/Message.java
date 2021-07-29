package com.example.go4lunch.models;

import java.util.Date;

public class Message {
    private String message;
    private Date dateCreated;
    private String idUserSender;
    private String idUserSenderIdReceiver;
    private String urlImageUserSender;

    private String urlImage;

    public Message() {}

    public Message(String message,String idUserSender, String idUserSenderIdUserReceiver,String urlImageUserSender, Date dateCreated) {
        this.message = message;
        this.idUserSender = idUserSender;
        this.idUserSenderIdReceiver = idUserSenderIdUserReceiver;
        this.urlImageUserSender = urlImageUserSender;
        this.dateCreated = dateCreated;
    }

    public Message(String message, String urlImage, String idUserSender,String idUserSenderIdUserReceiver,String urlImageUserSender, Date dateCreated) {
        this.message = message;
        this.urlImage = urlImage;
        this.idUserSender = idUserSender;
        this.idUserSenderIdReceiver = idUserSenderIdUserReceiver;
        this.urlImageUserSender = urlImageUserSender;
        this.dateCreated = dateCreated;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    public Date getDateCreated() { return dateCreated; }
    public String getUrlImage() { return urlImage; }
    public String getIdUserSender(){return idUserSender;}
    public String getIdUserSenderIdReceiver(){return idUserSenderIdReceiver;}
    public String getUrlImageUserSender(){return urlImageUserSender;}


    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
