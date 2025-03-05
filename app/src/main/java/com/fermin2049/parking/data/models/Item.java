package com.fermin2049.parking.data.models;

public class Item {
    private String id;
    private String title;
    private String description;
    private String picture_url;
    private String category_id;
    private int quantity;
    private String currency_id;
    private double unit_price;

    public Item(String id, String title, String description, String picture_url, String category_id, int quantity, String currency_id, double unit_price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picture_url = picture_url;
        this.category_id = category_id;
        this.quantity = quantity;
        this.currency_id = currency_id;
        this.unit_price = unit_price;
    }

    // Getters y setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPicture_url() {
        return picture_url;
    }
    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }
    public String getCategory_id() {
        return category_id;
    }
    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getCurrency_id() {
        return currency_id;
    }
    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }
    public double getUnit_price() {
        return unit_price;
    }
    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }
}
