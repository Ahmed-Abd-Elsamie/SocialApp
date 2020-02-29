package com.example.root.socialapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 06/08/18.
 */

public class Post implements Parcelable {

    private String id;
    private String uid;
    private String user_name;
    private String user_img;
    private String date;
    private String post_desc;
    private String post_img;
    private String comments_num;
    private String likes_num;
    private boolean isLiked;

    public Post(){

    }


    public Post(String id, String uid, String user_name, String user_img, String date, String post_desc, String post_img, String comments_num, String likes_num, boolean isLiked) {
        this.id = id;
        this.uid = uid;
        this.user_name = user_name;
        this.user_img = user_img;
        this.date = date;
        this.post_desc = post_desc;
        this.post_img = post_img;
        this.comments_num = comments_num;
        this.likes_num = likes_num;
        this.isLiked = isLiked;
    }


    protected Post(Parcel in) {
        id = in.readString();
        uid = in.readString();
        user_name = in.readString();
        user_img = in.readString();
        date = in.readString();
        post_desc = in.readString();
        post_img = in.readString();
        comments_num = in.readString();
        likes_num = in.readString();
        isLiked = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_desc() {
        return post_desc;
    }

    public void setPost_desc(String post_desc) {
        this.post_desc = post_desc;
    }

    public String getPost_img() {
        return post_img;
    }

    public void setPost_img(String post_img) {
        this.post_img = post_img;
    }

    public String getComments_num() {
        return comments_num;
    }

    public void setComments_num(String comments_num) {
        this.comments_num = comments_num;
    }

    public String getLikes_num() {
        return likes_num;
    }

    public void setLikes_num(String likes_num) {
        this.likes_num = likes_num;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(user_name);
        dest.writeString(user_img);
        dest.writeString(date);
        dest.writeString(post_desc);
        dest.writeString(post_img);
        dest.writeString(comments_num);
        dest.writeString(likes_num);
        dest.writeBooleanArray(new boolean[]{isLiked});
    }
}
