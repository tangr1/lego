package com.manbiwang.peatio.matching.model;

import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/20/17.
 */
public class OrderBook {

    private ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitAskOrders;
    private ConcurrentNavigableMap<Integer, Order> marketAskOrders;
    private ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitBidOrders;
    private ConcurrentNavigableMap<Integer, Order> marketBidOrders;

    public ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitAskOrders() {
        return limitAskOrders;
    }

    public void setLimitAskOrders(ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitAskOrders) {
        this.limitAskOrders = limitAskOrders;
    }

    public ConcurrentNavigableMap<Integer, Order> getMarketAskOrders() {
        return marketAskOrders;
    }

    public void setMarketAskOrders(ConcurrentNavigableMap<Integer, Order> marketAskOrders) {
        this.marketAskOrders = marketAskOrders;
    }

    public ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitBidOrders() {
        return limitBidOrders;
    }

    public void setLimitBidOrders(ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitBidOrders) {
        this.limitBidOrders = limitBidOrders;
    }

    public ConcurrentNavigableMap<Integer, Order> getMarketBidOrders() {
        return marketBidOrders;
    }

    public void setMarketBidOrders(ConcurrentNavigableMap<Integer, Order> marketBidOrders) {
        this.marketBidOrders = marketBidOrders;
    }
}
