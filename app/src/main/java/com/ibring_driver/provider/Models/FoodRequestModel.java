package com.ibring_driver.provider.Models;

import java.io.Serializable;

public class FoodRequestModel implements Serializable {

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getRest_loc() {
        return rest_loc;
    }

    public void setRest_loc(String rest_loc) {
        this.rest_loc = rest_loc;
    }

    public String getRest_lat() {
        return rest_lat;
    }

    public void setRest_lat(String rest_lat) {
        this.rest_lat = rest_lat;
    }

    public String getRest_lng() {
        return rest_lng;
    }

    public void setRest_lng(String rest_lng) {
        this.rest_lng = rest_lng;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_item() {
        return order_item;
    }

    public void setOrder_item(String order_item) {
        this.order_item = order_item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_loc() {
        return user_loc;
    }

    public void setUser_loc(String user_loc) {
        this.user_loc = user_loc;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String rest_id,rest_name,rest_loc,rest_lat,rest_lng,order_id,order_item,price,user_name,user_loc,lattitude,longitude;
}
