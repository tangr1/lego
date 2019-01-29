package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
@Service
public class MarketOrderHandler implements OrderHandler {

    @Autowired
    private PublishService publishService;

    @Override
    public boolean isFilled(Order order) {
        return BigDecimal.valueOf(order.getVolume()).compareTo(BigDecimal.ZERO) <= 0 ||
                BigDecimal.valueOf(order.getLocked()).compareTo(BigDecimal.ZERO) <= 0;
    }

    @Override
    public double getPrice(Trade trade) {
        if (trade.getCounter().getStrategy().equals(OrderStrategy.MARKET)) {
            return trade.getTopLimitOrder().getPrice();
        } else {
            return trade.getCounter().getPrice();
        }
    }

    @Override
    public double getVolume(Trade trade) {
        if (trade.getCounter().getStrategy().equals(OrderStrategy.MARKET)) {
            return Double.min(Double.min(Double.min(trade.getOrder().getVolume(), trade.getCounter().getVolume()),
                    trade.getCounterHandler().getVolumeLimit(trade.getCounter(), trade)),
                    trade.getOrderHandler().getVolumeLimit(trade.getOrder(), trade));
        } else {
            return Double.min(Double.min(trade.getOrder().getVolume(), trade.getCounter().getVolume()),
                    trade.getCounterHandler().getVolumeLimit(trade.getCounter(), trade));
        }
    }

    @Override
    public boolean remove(Order order, OrderBook orderBook) {
        ConcurrentNavigableMap<Integer, Order> marketOrders;
        if (order.getType().equals(OrderType.ASK)) {
            marketOrders = orderBook.getMarketAskOrders();
        } else {
            marketOrders = orderBook.getMarketBidOrders();
        }
        if (marketOrders.containsKey(order.getId())) {
            marketOrders.remove(order.getId());
            return true;
        }
        return false;
    }

    @Override
    public void finish(Order order, OrderBook orderBook) {
        publishService.publishCancelOrder(order);
    }
}
