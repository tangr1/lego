package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by tangrui on 8/19/17.
 */
@Service
public class LimitOrderHandler implements OrderHandler {

    @Autowired
    private PublishService publishService;

    @Override
    public boolean isFilled(Order order) {
        return BigDecimal.valueOf(order.getVolume()).compareTo(BigDecimal.ZERO) <= 0;
    }

    @Override
    public double getPrice(Trade trade) {
        if (trade.getCounter().getStrategy().equals(OrderStrategy.MARKET)) {
            return trade.getOrder().getPrice();
        } else {
            return trade.getCounter().getPrice();
        }
    }

    @Override
    public double getVolume(Trade trade) {
        if (trade.getCounter().getStrategy().equals(OrderStrategy.MARKET)) {
            return Double.min(Double.min(trade.getOrder().getVolume(), trade.getCounter().getVolume()),
                    trade.getCounterHandler().getVolumeLimit(trade.getCounter(), trade));
        } else {
            return Double.min(trade.getOrder().getVolume(), trade.getCounter().getVolume());
        }
    }

    @Override
    public boolean remove(Order order, OrderBook orderBook) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders;
        if (order.getType().equals(OrderType.ASK)) {
            limitOrders = orderBook.getLimitAskOrders();
        } else {
            limitOrders = orderBook.getLimitBidOrders();
        }
        if (limitOrders.containsKey(order.getPrice())) {
            ConcurrentNavigableMap<Integer, Order> counters = limitOrders.get(order.getPrice());
            if (counters.containsKey(order.getId())) {
                counters.remove(order.getId());
                return true;
            }
            if (counters.isEmpty()) {
                limitOrders.remove(order.getPrice());
            }
        }
        return false;
    }

    @Override
    public void finish(Order order, OrderBook orderBook) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders;
        if (order.getType().equals(OrderType.ASK)) {
            limitOrders = orderBook.getLimitAskOrders();
        } else {
            limitOrders = orderBook.getLimitBidOrders();
        }
        if (!limitOrders.containsKey(order.getPrice())) {
            limitOrders.put(order.getPrice(), new ConcurrentSkipListMap<>());
        }
        limitOrders.get(order.getPrice()).put(order.getId(), order);
        publishService.publishAddOrder(order);
    }
}
