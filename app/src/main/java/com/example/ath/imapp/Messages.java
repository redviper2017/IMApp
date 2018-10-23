package com.example.ath.imapp;



import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Messages implements Parcelable{

    private int id, senderId, receiverId;

    private String message, timestamp;

    public Messages(int id, int senderId, int receiverId, String message, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    protected Messages(Parcel in) {
        id = in.readInt();
        senderId = in.readInt();
        receiverId = in.readInt();
        message = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<Messages> CREATOR = new Creator<Messages>() {
        @Override
        public Messages createFromParcel(Parcel source) {
            return new Messages(source);
        }

        @Override
        public Messages[] newArray(int size) {
            return new Messages[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(senderId);
        dest.writeInt(receiverId);
        dest.writeString(message);
        dest.writeString(timestamp);
    }
}
