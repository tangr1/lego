package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
@Service
public class LimitAskOrderMatcher extends LimitOrderMatcher {

    @Autowired
    public PublishService publishService;

    @Override
    public Optional<Trade> match(Order order, OrderBook orderBook) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders
                = orderBook.getLimitBidOrders();
        ConcurrentNavigableMap<Integer, Order> marketOrders = orderBook.getMarketBidOrders();
        Order counter;
        if (marketOrders.isEmpty()) {
            if (limitOrders.isEmpty()) {
                return Optional.empty();
            }
            ConcurrentNavigableMap<Integer, Order> counters = limitOrders.firstEntry().getValue();
            if (counters.isEmpty()) {
                return Optional.empty();
            }
            counter = counters.firstEntry().getValue();
        } else {
            counter = marketOrders.get(marketOrders.firstKey());
        }
        if (counter.getStrategy().equals(OrderStrategy.LIMIT) && counter.getPrice() < order.getPrice()) {
            return Optional.empty();
        }
        Trade trade = new Trade();
        trade.setOrder(order);
        trade.setCounter(counter);
        return Optional.of(trade);
    }

    @Override
    ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitOrders(OrderBook orderBook) {
        return orderBook.getLimitAskOrders();
    }

    @Override
    PublishService getPublishService() {
        return publishService;
    }
}
