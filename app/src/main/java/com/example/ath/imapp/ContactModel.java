package com.example.ath.imapp;

import android.graphics.Bitmap;

public class ContactModel {

    private String name, number;
    private Bitmap imageUri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Bitmap getImageUri() {
        return imageUri;
    }

    public void setImageUri(Bitmap imageUri) {
        this.imageUri = imageUri;
    }
}