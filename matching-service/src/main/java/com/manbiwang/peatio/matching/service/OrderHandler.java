package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.Order;
import com.manbiwang.peatio.matching.model.OrderBook;
import com.manbiwang.peatio.matching.model.Trade;

/**
 * Created by tangrui on 8/19/17.
 */
public interface OrderHandler {

    boolean isFilled(Order order);

    default boolean isCrossed(Trade trade) {
        if (trade.isAsk()) {
            return trade.getOrder().getPrice() <= trade.getCounter().getPrice();
        } else {
            return trade.getCounter().getPrice() <= trade.getOrder().getPrice();
        }
    }

    double getPrice(Trade trade);

    double getVolume(Trade trade);

    default double getVolumeLimit(Order order, Trade trade) {
        if (trade.isAsk()) {
            return order.getLocked();
        } else {
            return order.getLocked() / trade.getPrice();
        }
    }

    void finish(Order order, OrderBook orderBook);

    boolean remove(Order order, OrderBook orderBook);
}
