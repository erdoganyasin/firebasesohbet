package com.saginsoft.firebasesohbet;

public class UserInfo {
    String uid;
    String name;
    String photoUrl;

    public UserInfo(String uid, String name, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public UserInfo(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
