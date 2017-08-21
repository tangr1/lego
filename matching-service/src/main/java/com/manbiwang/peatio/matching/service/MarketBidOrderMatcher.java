package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
public class MarketBidOrderMatcher extends MarketOrderMatcher {

    @Autowired
    private PublishService publishService;

    public boolean isFilled(Order order) {
        return BigDecimal.valueOf(order.getVolume()).compareTo(BigDecimal.ZERO) <= 0 ||
                BigDecimal.valueOf(order.getLocked()).compareTo(BigDecimal.ZERO) <= 0;
    }

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

    ConcurrentNavigableMap<Integer, Order> getMarketOrders(OrderBook orderBook) {
        return orderBook.getMarketAskOrders();
    }

    PublishService getPublishService() {
        return publishService;
    }

    @Override
    Optional<Double> getBestLimitPrice(Trade trade) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders =
                trade.getOrderBook().getLimitAskOrders();
        if (limitOrders.isEmpty()) {
            return Optional.empty();
        }
        if (limitOrders.lastEntry().getValue().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(limitOrders.lastEntry().getKey());
    }
}
