package com.ibring_driver.provider.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodModel implements Serializable {
    String id,order_id,rest_id,rest_name,rest_address,rest_lat,rest_lng,price,status,date_time,delivery_person_name,delivery_phone,rest_phone,
    drop_address,drop_lat,drop_lng,payment_mode,payment_status,rest_image,rest_description,delivery_boy_id;

    ArrayList<FoodItemModel> list;

    public ArrayList<FoodItemModel> getList() {
        return list;
    }

    public void setList(ArrayList<FoodItemModel> list) {
        this.list = list;
    }

    public String getRest_image() {
        return rest_image;
    }

    public String getDelivery_boy_id() {
        return delivery_boy_id;
    }

    public void setDelivery_boy_id(String delivery_boy_id) {
        this.delivery_boy_id = delivery_boy_id;
    }

    public void setRest_image(String rest_image) {
        this.rest_image = rest_image;
    }

    public String getRest_description() {
        return rest_description;
    }

    public void setRest_description(String rest_description) {
        this.rest_description = rest_description;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_lng() {
        return drop_lng;
    }

    public void setDrop_lng(String drop_lng) {
        this.drop_lng = drop_lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

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

    public String getRest_address() {
        return rest_address;
    }

    public void setRest_address(String rest_address) {
        this.rest_address = rest_address;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDelivery_person_name() {
        return delivery_person_name;
    }

    public void setDelivery_person_name(String delivery_person_name) {
        this.delivery_person_name = delivery_person_name;
    }

    public String getDelivery_phone() {
        return delivery_phone;
    }

    public void setDelivery_phone(String delivery_phone) {
        this.delivery_phone = delivery_phone;
    }

    public String getRest_phone() {
        return rest_phone;
    }

    public void setRest_phone(String rest_phone) {
        this.rest_phone = rest_phone;
    }
}
