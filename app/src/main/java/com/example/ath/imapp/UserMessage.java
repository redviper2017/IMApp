package com.example.ath.imapp;

public class UserMessage {

    private String senderName,senderImage,message,time,receiverName;


    public UserMessage(String senderName, String senderImage, String message, String time,String receiverName) {
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.message = message;
        this.time = time;
        this.receiverName = receiverName;

    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String  getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
