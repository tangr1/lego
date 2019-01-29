package com.manbiwang.peatio.matching.model;

import com.manbiwang.peatio.matching.service.OrderHandler;

/**
 * Created by tangrui on 8/17/17.
 */
public class Trade {

    private Order order;
    private Order topLimitOrder;
    private Order counter;
    private Market market;
    private OrderBook orderBook;
    private Double price;
    private Double volume;
    private Double funds;
    private boolean ask;
    private OrderHandler orderHandler;
    private OrderHandler counterHandler;

    public Trade() {

    }

    public Trade(Order order, OrderHandler orderHandler, Market market, OrderBook orderBook) {
        this.order = order;
        this.orderHandler = orderHandler;
        this.ask = order.getType().equals(OrderType.ASK);
        this.orderBook = orderBook;
        this.market = market;
    }

    public Order getTopLimitOrder() {
        return topLimitOrder;
    }

    public void setTopLimitOrder(Order topLimitOrder) {
        this.topLimitOrder = topLimitOrder;
    }

    public boolean isAsk() {
        return ask;
    }

    public void setAsk(boolean ask) {
        this.ask = ask;
    }

    public OrderHandler getOrderHandler() {
        return orderHandler;
    }

    public void setOrderHandler(OrderHandler orderHandler) {
        this.orderHandler = orderHandler;
    }

    public OrderHandler getCounterHandler() {
        return counterHandler;
    }

    public void setCounterHandler(OrderHandler counterHandler) {
        this.counterHandler = counterHandler;
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

