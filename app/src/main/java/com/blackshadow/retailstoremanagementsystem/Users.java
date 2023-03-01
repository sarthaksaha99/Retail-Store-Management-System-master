package com.blackshadow.retailstoremanagementsystem;

public class Users {
    private String name,phone,uid,user_type,address;

    public Users() {
    }

    public Users(String name, String phone, String uid, String user_type, String address) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.user_type = user_type;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
