package com.blackshadow.retailstoremanagementsystem;

import com.google.firebase.database.Exclude;

public class product {
    private String name;
    private String imageURL;
    private String key;
    private String price;
    private int position;

    public product() {
        //empty constructor needed
    }
    public product (int position){
        this.position = position;
    }
    public product(String name, String imageUrl ,String Des) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name = name;
        this.imageURL = imageUrl;
        this.price = Des;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImageUrl() {
        return imageURL;
    }
    public void setImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
    }
    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
