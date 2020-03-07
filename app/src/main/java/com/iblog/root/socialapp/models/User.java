package com.iblog.root.socialapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 16/08/18.
 */

public class User implements Parcelable {

    private String id;
    private String name;
    private String email;
    private String img;
    private String token_id;

    public User(){

    }

    public User(String id, String name, String email, String img, String token_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.img = img;
        this.token_id = token_id;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        img = in.readString();
        token_id = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(img);
        dest.writeString(token_id);
    }
}
