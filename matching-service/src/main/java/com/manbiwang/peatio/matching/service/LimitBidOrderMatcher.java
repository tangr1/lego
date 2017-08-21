package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by tangrui on 8/19/17.
 */
@Service
public class LimitBidOrderMatcher extends LimitOrderMatcher {

    @Autowired
    public PublishService publishService;

    @Override
    public Optional<Trade> match(Order order, OrderBook orderBook) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders
                = orderBook.getLimitAskOrders();
        ConcurrentNavigableMap<Integer, Order> marketOrders = orderBook.getMarketAskOrders();
        Order counter;
        if (marketOrders.isEmpty()) {
            if (limitOrders.isEmpty()) {
                return Optional.empty();
            }
            ConcurrentNavigableMap<Integer, Order> counters = limitOrders.lastEntry().getValue();
            if (counters.isEmpty()) {
                return Optional.empty();
            }
            counter = counters.firstEntry().getValue();
        } else {
            counter = marketOrders.get(marketOrders.firstKey());
        }
        if (counter.getStrategy().equals(OrderStrategy.LIMIT) && order.getPrice() < counter.getPrice()) {
            return Optional.empty();
        }
        Trade trade = new Trade();
        trade.setOrder(order);
        trade.setCounter(counter);
        return Optional.of(trade);
    }

    @Override
    ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitOrders(OrderBook orderBook) {
        return orderBook.getLimitBidOrders();
    }

    @Override
    PublishService getPublishService() {
        return publishService;
    }
}
