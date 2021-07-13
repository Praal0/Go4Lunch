package com.example.go4lunch.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class User implements Parcelable {
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


    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        urlPicture = in.readString();
        byte tmpNotification = in.readByte();
        notification = tmpNotification == 1;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.username);
        dest.writeString(this.urlPicture);
        dest.writeInt(notification ? 1 : 0);
    }
}
