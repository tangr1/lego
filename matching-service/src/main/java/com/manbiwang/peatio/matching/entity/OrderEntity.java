package com.manbiwang.peatio.matching.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tangrui on 8/17/17.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer bid;
    private Integer ask;
    private Integer currency;
    private Double price;
    private Double volume;
    private Double originVolume;
    private Integer state;
    private Date doneAt;
    private String type;
    private Integer memberId;
    private Date createdAt;
    private Date updatedAt;
    private String sn;
    private String source;
    @Column(name = "ordType")
    private String strategy;
    private Double locked;
    private Double originLocked;
    private Double fundsReceived;
    private Integer tradesCount;

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

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public Integer getAsk() {
        return ask;
    }

    public void setAsk(Integer ask) {
        this.ask = ask;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getOriginVolume() {
        return originVolume;
    }

    public void setOriginVolume(Double originVolume) {
        this.originVolume = originVolume;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(Date doneAt) {
        this.doneAt = doneAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getLocked() {
        return locked;
    }

    public void setLocked(Double locked) {
        this.locked = locked;
    }

    public Double getOriginLocked() {
        return originLocked;
    }

    public void setOriginLocked(Double originLocked) {
        this.originLocked = originLocked;
    }

    public Double getFundsReceived() {
        return fundsReceived;
    }

    public void setFundsReceived(Double fundsReceived) {
        this.fundsReceived = fundsReceived;
    }

    public Integer getTradesCount() {
        return tradesCount;
    }

    public void setTradesCount(Integer tradesCount) {
        this.tradesCount = tradesCount;
    }
}
