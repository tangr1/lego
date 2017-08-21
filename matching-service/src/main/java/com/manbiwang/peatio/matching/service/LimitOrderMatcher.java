package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by tangrui on 8/19/17.
 */
public abstract class LimitOrderMatcher implements OrderMatcher {

    private final static Logger logger = LoggerFactory.getLogger(LimitOrderMatcher.class);

    @Override
    public boolean isFilled(Order order) {
        return BigDecimal.valueOf(order.getVolume()).compareTo(BigDecimal.ZERO) <= 0;
    }

    @Override
    abstract public Optional<Trade> match(Order order, OrderBook orderBook);

    abstract ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> getLimitOrders(
            OrderBook orderBook);

    abstract PublishService getPublishService();

    @Override
    public Optional<Trade> trade(Trade trade) {
        Order order = trade.getOrder();
        Order counter = trade.getCounter();
        Market market = trade.getMarket();
        if (counter.getStrategy().equals(OrderStrategy.LIMIT)) {
            trade.setPrice(BigDecimal.valueOf(counter.getPrice())
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setVolume(BigDecimal.valueOf(Double.min(order.getVolume(), counter.getVolume()))
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setFunds(trade.getPrice() * trade.getVolume());
        } else {
            trade.setPrice(BigDecimal.valueOf(order.getPrice())
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setVolume(BigDecimal.valueOf(Double.min(Double.min(order.getVolume(), counter.getVolume()),
                    order.getLocked()))
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setFunds(trade.getPrice() * trade.getVolume());
        }
        return Optional.of(trade);
    }

    @Override
    public Optional<Trade> fillOrder(Trade trade) {
        Order order = trade.getOrder();
        if (trade.getVolume() > order.getVolume()) {
            logger.error("交易需要数量{}，而订单{}当前数量为{}", trade.getVolume(), order.getId(), order.getVolume());
            return Optional.empty();
        }
        order.setVolume(order.getVolume() - trade.getVolume());
        return Optional.of(trade);
    }

    private boolean remove(Order order,
                           ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders) {
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
    public Optional<Trade> fillCounter(Trade trade) {
        if (!fillOrder(trade).isPresent()) {
            return Optional.empty();
        }
        if (isFilled(trade.getCounter()) && remove(trade.getCounter(), getLimitOrders(trade.getOrderBook()))) {
            getPublishService().broadcastRemove(trade.getCounter());
        } else {
            getPublishService().broadcastUpdate(trade.getCounter());
        }
        return Optional.of(trade);
    }

    @Override
    public void finish(Order order, OrderBook orderBook) {
        if (isFilled(order)) {
            return;
        }
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders =
                getLimitOrders(orderBook);
        if (!limitOrders.containsKey(order.getPrice())) {
            limitOrders.put(order.getPrice(), new ConcurrentSkipListMap<>());
        }
        limitOrders.get(order.getPrice()).put(order.getId(), order);
        getPublishService().publishAddOrder(order);
    }

    @Override
    public void cancel(Order order, OrderBook orderBook) {
        if (remove(order, getLimitOrders(orderBook))) {
            getPublishService().publishCancelOrder(order);
        }
    }
}
