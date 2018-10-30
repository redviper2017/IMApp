package com.example.ath.imapp;



import android.os.Parcel;
import android.os.Parcelable;

public class Messages implements Parcelable{

    private int id, sender_id, receiver_id;

    private String message, timestamp;

    public Messages(int sender_id, int receiver_id, String message, String timestamp) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Messages(int id, int sender_id, int receiver_id, String message, String timestamp) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    protected Messages(Parcel in) {
        id = in.readInt();
        sender_id = in.readInt();
        receiver_id = in.readInt();
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

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiverId() {
        return receiver_id;
    }

    public void setReceiverId(int receiver_id) {
        this.receiver_id = receiver_id;
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
        dest.writeInt(sender_id);
        dest.writeInt(receiver_id);
        dest.writeString(message);
        dest.writeString(timestamp);
    }
}