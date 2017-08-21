package com.manbiwang.peatio.matching.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by tangrui on 8/17/17.
 */
public class Order {

    private Integer id;
    private String market;
    private String type;
    @JsonProperty("ord_type")
    private String strategy;
    private Double volume;
    private Double price;
    private Double locked;
    private Long timestamp;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLocked() {
        return locked;
    }

    public void setLocked(Double locked) {
        this.locked = locked;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
