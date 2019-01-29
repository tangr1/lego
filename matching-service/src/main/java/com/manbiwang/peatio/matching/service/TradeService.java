package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.modelmapper.ModelMapper;
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
public class TradeService {

    private final static Logger logger = LoggerFactory.getLogger(TradeService.class);
    @Autowired
    private PublishService publishService;
    @Autowired
    private ModelMapper modelMapper;

    Optional<Trade> match(Trade trade) {
        OrderHandler orderHandler = trade.getOrderHandler();
        Order counter = getMarketCounter(trade).orElse(getLimitCounter(trade).orElse(null));
        if (counter == null) {
            return Optional.empty();
        }
        if (!orderHandler.isCrossed(trade)) {
            return Optional.empty();
        }
        if (trade.getOrder().getStrategy().equals(OrderStrategy.MARKET)
                && trade.getCounter().getStrategy().equals(OrderStrategy.MARKET)) {
            trade.setTopLimitOrder(getLimitCounter(trade).orElse(null));
            if (trade.getTopLimitOrder() == null) {
                return Optional.empty();
            }
        }
        trade.setCounter(counter);
        int fixed = getFixed(trade);
        trade.setPrice(round(orderHandler.getPrice(trade), fixed));
        trade.setVolume(round(orderHandler.getVolume(trade), fixed));
        trade.setFunds(round(trade.getPrice() * trade.getVolume(), fixed));
        return Optional.of(trade);
    }

    Optional<Trade> trade(Trade trade) {
        Order order = modelMapper.map(trade.getOrder(), Order.class);
        Order counter = modelMapper.map(trade.getOrder(), Order.class);
        Optional<Trade> optional = fill(order, trade).flatMap(trade1 -> fillCounter(counter, trade1));
        if (optional.isPresent()) {
            modelMapper.map(order, trade.getOrder());
            modelMapper.map(counter, trade.getCounter());
        }
        return optional;
    }

    void finish(Order order, OrderHandler orderHandler, OrderBook orderBook) {
        if (orderHandler.isFilled(order)) {
            return;
        }
        orderHandler.finish(order, orderBook);
    }

    void cancel(Order order, OrderHandler orderHandler, OrderBook orderBook) {
        if (orderHandler.remove(order, orderBook)) {
            publishService.publishCancelOrder(order);
        }
    }

    private Optional<Order> getLimitCounter(Trade trade) {
        ConcurrentNavigableMap<Double, ConcurrentNavigableMap<Integer, Order>> limitOrders;
        ConcurrentNavigableMap<Integer, Order> counters;
        if (trade.isAsk()) {
            limitOrders = trade.getOrderBook().getLimitBidOrders();
        } else {
            limitOrders = trade.getOrderBook().getLimitAskOrders();
        }
        if (limitOrders.isEmpty()) {
            return Optional.empty();
        }
        counters = limitOrders.lastEntry().getValue();
        if (counters.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(counters.firstEntry().getValue());
    }

    private Optional<Order> getMarketCounter(Trade trade) {
        ConcurrentNavigableMap<Integer, Order> counters;
        if (trade.isAsk()) {
            counters = trade.getOrderBook().getMarketBidOrders();
        } else {
            counters = trade.getOrderBook().getMarketAskOrders();
        }
        if (counters.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(counters.firstEntry().getValue());
        }
    }

    private Double round(Double input, int fixed) {
        return BigDecimal.valueOf(input).setScale(fixed, RoundingMode.HALF_UP).doubleValue();
    }

    private int getFixed(Trade trade) {
        if (trade.isAsk()) {
            return trade.getMarket().getAskFixed();
        } else {
            return trade.getMarket().getBidFixed();
        }
    }

    private Double getFunds(Trade trade) {
        if (trade.getOrder().getType().equals(OrderType.ASK)) {
            return trade.getVolume();
        } else {
            return trade.getFunds();
        }
    }

    private Optional<Trade> fill(Order order, Trade trade) {
        if (trade.getVolume() > order.getVolume()) {
            logger.error("交易需要数量{}，而订单{}当前数量为{}", trade.getVolume(), order.getId(), order.getVolume());
            return Optional.empty();
        }
        double originalVolume = order.getVolume();
        order.setVolume(order.getVolume() - trade.getVolume());
        if (order.getStrategy().equals(OrderStrategy.MARKET)) {
            Double funds = getFunds(trade);
            if (funds > order.getLocked()) {
                logger.error("交易需要金额{}，而订单{}当前锁定只能交易{}", funds, order.getId(), order.getLocked());
                order.setVolume(originalVolume);
                return Optional.empty();
            }
            order.setLocked(order.getLocked() - funds);
        }
        return Optional.of(trade);
    }

    private Optional<Trade> fillCounter(Order counter, Trade trade) {
        return fill(counter, trade).flatMap(trade1 -> {
            OrderHandler counterHandler = trade1.getCounterHandler();
            if (counterHandler.isFilled(trade1.getCounter()) &&
                    counterHandler.remove(trade1.getCounter(), trade1.getOrderBook())) {
                publishService.broadcastRemove(trade1.getCounter());
            } else {
                publishService.broadcastUpdate(trade1.getCounter());
            }
            return Optional.of(trade1);
        });
    }
}
