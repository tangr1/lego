package com.manbiwang.peatio.matching.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tangrui on 8/17/17.
 */
public class Order {

    private int id;
    private String market;
    private String type;
    @JsonProperty("ord_type")
    private String strategy;
    private double volume;
    private double price;
    private double locked;
    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLocked() {
        return locked;
    }

    public void setLocked(double locked) {
        this.locked = locked;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
