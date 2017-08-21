package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.Order;
import com.manbiwang.peatio.matching.model.OrderBook;
import com.manbiwang.peatio.matching.model.Trade;

import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
public interface OrderMatcher {

    boolean isFilled(Order order);

    Optional<Trade> match(Order order, OrderBook orderBook);

    Optional<Trade> trade(Trade trade);

    Optional<Trade> fillOrder(Trade trade);

    Optional<Trade> fillCounter(Trade trade);

    void finish(Order order, OrderBook orderBook);

    void cancel(Order order, OrderBook orderBook);

    ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitOrders(OrderBook orderBook);

    ConcurrentNavigableMap<Integer, Order> getMarketOrders(OrderBook orderBook);

    Optional<Order> getLimitCounter(ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders);

    Optional<Order> getMarketCounter(ConcurrentNavigableMap<Integer, Order> marketOrders);

    boolean isCrossed(Trade trade);

    Double getPrice(Trade trade);

    Double getVolume(Trade trade);

    Double getFunds(Trade trade);
}
