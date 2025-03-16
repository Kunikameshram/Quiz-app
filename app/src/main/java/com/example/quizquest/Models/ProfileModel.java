package com.example.quizquest.Models;

public class ProfileModel {

    private String name;
    private String photoURL;
    private String emailID;
    private String phoneNo;
    private String state;

    public ProfileModel(String name, String photoURL, String emailID, String phoneNo, String state) {
        this.name = name;
        this.photoURL = photoURL;
        this.emailID = emailID;
        this.phoneNo = phoneNo;
        this.state = state;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
