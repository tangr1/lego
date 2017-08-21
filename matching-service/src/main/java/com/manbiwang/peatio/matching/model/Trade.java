package com.manbiwang.peatio.matching.model;

import com.manbiwang.peatio.matching.service.OrderMatcher;

/**
 * Created by tangrui on 8/17/17.
 */
public class Trade {

    private Order order;
    private Order counter;
    private Market market;
    private OrderBook orderBook;
    private Double price;
    private Double volume;
    private Double funds;

    public OrderMatcher getOrderMatcher() {
        return orderMatcher;
    }

    public void setOrderMatcher(OrderMatcher orderMatcher) {
        this.orderMatcher = orderMatcher;
    }

    public OrderMatcher getCounterMatcher() {
        return counterMatcher;
    }

    public void setCounterMatcher(OrderMatcher counterMatcher) {
        this.counterMatcher = counterMatcher;
    }

    private OrderMatcher orderMatcher;
    private OrderMatcher counterMatcher;

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

    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getCounter() {
        return counter;
    }

    public void setCounter(Order counter) {
        this.counter = counter;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }
}

