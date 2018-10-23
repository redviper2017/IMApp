package com.example.ath.imapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Personnel implements Parcelable{

    private Integer id;

    private String firstName, lastName, email, image, mobile;

    public Personnel(Integer id, String firstName, String lastName, String email, String image, String mobile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
        this.mobile = mobile;
    }

    protected Personnel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        image = in.readString();
        mobile = in.readString();
    }

    public static final Creator<Personnel> CREATOR = new Creator<Personnel>() {
        @Override
        public Personnel createFromParcel(Parcel in) {
            return new Personnel(in);
        }

        @Override
        public Personnel[] newArray(int size) {
            return new Personnel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIamge() {
        return image;
    }

    public void setIamge(String image) {
        this.image = image;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(image);
        dest.writeString(mobile);
    }
}
