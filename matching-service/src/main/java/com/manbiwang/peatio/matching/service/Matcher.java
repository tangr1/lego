package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.exception.CannotFindOrderBookException;
import com.manbiwang.peatio.matching.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by tangrui on 8/19/17.
 */
@Service
public class Matcher {

    private final static Logger logger = LoggerFactory.getLogger(Matcher.class);
    @Autowired
    private PublishService publishService;

    private Optional<Order> getMarketCounter(ConcurrentNavigableMap<Integer, Order> marketOrders) {
        if (marketOrders.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(marketOrders.firstEntry().getValue());
        }
    }

    public Optional<Trade> match(Trade trade) {
        OrderMatcher orderMatcher = trade.getOrderMatcher();
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders
                = orderMatcher.getLimitOrders(trade.getOrderBook());
        ConcurrentNavigableMap<Integer, Order> marketOrders = orderMatcher.getMarketOrders(trade.getOrderBook());
        Order counter = orderMatcher.getMarketCounter(marketOrders)
                .orElse(orderMatcher.getLimitCounter(limitOrders)
                        .orElse(null));
        if (counter == null) {
            return Optional.empty();
        }
        if (!orderMatcher.isCrossed(trade)) {
            return Optional.empty();
        }
        trade.setCounter(counter);
        return Optional.of(trade);
    }

    private Double round(Double input, int fixed) {
        return BigDecimal.valueOf(input).setScale(fixed, RoundingMode.HALF_UP).doubleValue();
    }

    public Optional<Trade> trade(Trade trade) {
        OrderMatcher orderMatcher = trade.getOrderMatcher();
        trade.setPrice(orderMatcher.getPrice(trade));
        trade.setVolume(orderMatcher.getVolume(trade));
        trade.setFunds(trade.getPrice() * trade.getVolume());
        return Optional.of(trade);
    }

    public Optional<Trade> fillOrder(Trade trade) {
        Order order = trade.getOrder();
        if (trade.getVolume() > order.getVolume()) {
            logger.error("交易需要数量{}，而订单{}当前数量为{}", trade.getVolume(), order.getId(), order.getVolume());
            return Optional.empty();
        }
        order.setVolume(order.getVolume() - trade.getVolume());
        if (order.getStrategy().equals(OrderStrategy.MARKET)) {
            Double funds = trade.getOrderMatcher().getFunds(trade);
            if (funds > order.getLocked()) {
                logger.error("交易需要金额{}，而订单{}当前锁定只能交易{}", funds, order.getId(), order.getLocked());
                return Optional.empty();
            }
            order.setLocked(order.getLocked() - funds);
        }
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

    private boolean remove(Order order,
                           ConcurrentNavigableMap<Integer, Order> marketOrders) {
        if (marketOrders.containsKey(order.getId())) {
            marketOrders.remove(order.getId());
            return true;
        }
        return false;
    }

    public Optional<Trade> fillCounter(Trade trade) {
        if (!fillOrder(trade).isPresent()) {
            return Optional.empty();
        }
        OrderMatcher counterMatcher = trade.getCounterMatcher();
        if (counterMatcher.isFilled(trade.getCounter()) &&
                counterMatcher.remove(trade.getCounter(), counterMatcher.getLimitOrders(trade.getOrderBook()))) {
            publishService.broadcastRemove(trade.getCounter());
        } else {
            publishService.broadcastUpdate(trade.getCounter());
        }
        return Optional.of(trade);
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
