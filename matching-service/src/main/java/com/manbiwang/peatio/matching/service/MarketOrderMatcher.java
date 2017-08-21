package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
public abstract class MarketOrderMatcher implements OrderMatcher {

    private final static Logger logger = LoggerFactory.getLogger(LimitOrderMatcher.class);

    @Override
    public boolean isFilled(Order order) {
        return BigDecimal.valueOf(order.getVolume()).compareTo(BigDecimal.ZERO) <= 0 ||
                BigDecimal.valueOf(order.getLocked()).compareTo(BigDecimal.ZERO) <= 0;
    }

    @Override
    abstract public Optional<Trade> match(Order order, OrderBook orderBook);

    abstract ConcurrentNavigableMap<Integer, Order> getMarketOrders(OrderBook orderBook);

    abstract PublishService getPublishService();

    abstract Optional<Double> getBestLimitPrice(Trade trade);

    private Double getFunds(Trade trade) {
        if (trade.getOrder().getType().equals(OrderType.ASK)) {
            return trade.getVolume();
        } else {
            return trade.getFunds();
        }
    }

    private Double getVolumeLimit(Trade trade) {
        if (trade.getOrder().getType().equals(OrderType.ASK)) {
            return trade.getOrder().getLocked();
        } else {
            return trade.getOrder().getLocked() / trade.getPrice();
        }
    }

    private Double getCounterVolumeLimit(Trade trade) {
        if (trade.getCounter().getType().equals(OrderType.ASK)) {
            return trade.getCounter().getLocked();
        } else {
            return trade.getCounter().getLocked() / trade.getPrice();
        }
    }

    @Override
    public Optional<Trade> trade(Trade trade) {
        Order order = trade.getOrder();
        Order counter = trade.getCounter();
        Market market = trade.getMarket();
        if (counter.getStrategy().equals(OrderStrategy.LIMIT)) {
            trade.setPrice(BigDecimal.valueOf(counter.getPrice())
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setVolume(BigDecimal.valueOf(Double.min(Double.min(order.getVolume(), counter.getVolume()),
                    getVolumeLimit(trade))).setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setFunds(trade.getPrice() * trade.getVolume());
        } else {
            Optional<Double> price = getBestLimitPrice(trade);
            if (!price.isPresent()) {
                return Optional.empty();
            }
            trade.setPrice(BigDecimal.valueOf(price.get())
                    .setScale(market.getBidFixed(), RoundingMode.HALF_UP).doubleValue());
            trade.setVolume(BigDecimal.valueOf(Double.min(Double.min(Double.min(order.getVolume(), counter.getVolume()),
                    getVolumeLimit(trade)), getCounterVolumeLimit(trade)))
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
        Double funds = getFunds(trade);
        if (funds > order.getLocked()) {
            logger.error("交易需要金额{}，而订单{}当前锁定只能交易{}", funds, order.getId(), order.getLocked());
            return Optional.empty();
        }
        order.setLocked(order.getLocked() - funds);
        return Optional.of(trade);
    }

    private boolean remove(Order order,
                           ConcurrentNavigableMap<Integer, Order> marketOrders) {
        if (marketOrders.containsKey(order.getId())) {
            marketOrders.remove(order.getId());
            return true;
        }
        return false;
    }

    @Override
    public Optional<Trade> fillCounter(Trade trade) {
        if (!fillOrder(trade).isPresent()) {
            return Optional.empty();
        }
        if (isFilled(trade.getCounter()) && remove(trade.getCounter(), getMarketOrders(trade.getOrderBook()))) {
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
        getPublishService().publishCancelOrder(order);
    }

    @Override
    public void cancel(Order order, OrderBook orderBook) {
        if (remove(order, getMarketOrders(orderBook))) {
            getPublishService().publishCancelOrder(order);
        }
    }
}
