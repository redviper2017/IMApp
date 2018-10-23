package com.example.ath.imapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable{

    private String senderName, senderMessage, senderImageUri;

    private int receiverId;

//    public ChatModel(String senderName, String senderMessage) {
//        this.senderName = senderName;
//        this.senderMessage = senderMessage;
//    }

    public ChatModel(String senderName, String senderMessage, String senderImageUri, int receiverId) {
        this.senderName = senderName;
        this.senderMessage = senderMessage;
        this.senderImageUri = senderImageUri;
        this.receiverId = receiverId;
    }
    //    private Bitmap senderImageUri;

    protected ChatModel(Parcel in) {
        senderName = in.readString();
        senderMessage = in.readString();
        senderImageUri = in.readString();
        receiverId = in.readInt();
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel source) {
            return new ChatModel(source);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    public String getSenderImageUri() {
        return senderImageUri;
    }

    public void setSenderImageUri(String senderImageUri) {
        this.senderImageUri = senderImageUri;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderName);
        dest.writeString(senderMessage);
        dest.writeString(senderImageUri);
        dest.writeInt(receiverId);
    }
}
