package com.manbiwang.peatio.matching.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.manbiwang.peatio.matching.config.MatchingConfig;
import com.manbiwang.peatio.matching.entity.OrderEntity;
import com.manbiwang.peatio.matching.exception.CannotFindMarketException;
import com.manbiwang.peatio.matching.exception.CannotFindOrderMatcherException;
import com.manbiwang.peatio.matching.exception.CannotFindOrderBookException;
import com.manbiwang.peatio.matching.model.*;
import com.manbiwang.peatio.matching.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * Created by tangrui on 8/17/17.
 */
@Service
public class MatchingService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MatchingConfig matchingConfig;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PublishService publishService;
    private Map<Integer, OrderBook> orderBooks;
    private Table<String, String, OrderMatcher> matchers;
    private Map<String, Market> markets;

    @PostConstruct
    public void setup() {
        orderBooks = new ConcurrentHashMap<>();
        matchingConfig.getMarkets().forEach(market -> {
            OrderBook orderBook = new OrderBook();
            orderBook.setLimitAskOrders(new ConcurrentSkipListMap<>());
            orderBook.setLimitBidOrders(new ConcurrentSkipListMap<>());
            orderBook.setMarketAskOrders(new ConcurrentSkipListMap<>());
            orderBook.setMarketBidOrders(new ConcurrentSkipListMap<>());
            orderBooks.put(market.getCode(), orderBook);
            publishService.broadcast(new HashMap<String, String>() {{
                put("action", "new");
                put("market", market.getCode() + "");
                put("side", "ask");
            }});
            publishService.broadcast(new HashMap<String, String>() {{
                put("action", "new");
                put("market", market.getCode() + "");
                put("side", "bid");
            }});
        });
        matchers = HashBasedTable.create();
        matchers.put(OrderStrategy.LIMIT, OrderType.ASK, new LimitAskOrderMatcher());
        matchers.put(OrderStrategy.LIMIT, OrderType.BID, new LimitBidOrderMatcher());
        matchers.put(OrderStrategy.MARKET, OrderType.ASK, new MarketAskOrderMatcher());
        matchers.put(OrderStrategy.MARKET, OrderType.BID, new MarketAskOrderMatcher());
        markets = matchingConfig.getMarkets().stream().collect(Collectors.toMap(Market::getId, e -> e));
        matchingConfig.getMarkets().forEach(market ->
                orderRepository.findByCurrencyAndStateOrderById(market.getCode(), OrderState.WAIT)
                        .stream().map(this::toModel).forEach(this::submit));
    }

    void processOrderRequest(OrderRequest orderRequest) {
        switch(orderRequest.getAction()) {
            case "submit":
                if (orderRequest.getOrder() != null) {
                    submit(orderRequest.getOrder());
                }
                break;
            case "cancel":
                if (orderRequest.getOrder() != null) {
                    cancel(orderRequest.getOrder());
                }
                break;
            case "reload":
                setup();
                break;
            default:
                break;
        }
    }

    private void submit(Order order) {
        OrderMatcher orderMatcher = getMatcher(order).orElseThrow(CannotFindOrderMatcherException::new);
        Market market = getMarket(order.getMarket()).orElseThrow(CannotFindMarketException::new);
        OrderBook orderBook = getOrderBook(market.getCode()).orElseThrow(CannotFindOrderBookException::new);
        // 不断匹配订单做交易，直到volume耗尽
        while (!orderMatcher.isFilled(order)) {
            Trade trade = orderMatcher.match(order, orderBook)
                    .flatMap(orderMatcher::trade)
                    .orElseGet(Trade::new);
            // 说明完全找不到可配对的订单，马上退出循环
            if (trade.getCounter() == null) {
                break;
            }
            getMatcher(trade.getCounter())
                    .flatMap(counterMatcher -> counterMatcher.fillCounter(trade))
                    .flatMap(orderMatcher::fillOrder)
                    .ifPresent(publishService::publishTrade);
        }
        orderMatcher.finish(order, orderBook);
    }

    private void cancel(Order order) {
        OrderMatcher orderMatcher = getMatcher(order).orElseThrow(CannotFindOrderMatcherException::new);
        Market market = getMarket(order.getMarket()).orElseThrow(CannotFindMarketException::new);
        OrderBook orderBook = getOrderBook(market.getCode()).orElseThrow(CannotFindOrderBookException::new);
        orderMatcher.cancel(order, orderBook);
    }

    private Optional<OrderMatcher> getMatcher(Order order) {
        if (matchers.contains(order.getStrategy(), order.getType())) {
            return Optional.of(matchers.get(order.getStrategy(), order.getType()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<OrderBook> getOrderBook(Integer marketCode) {
        if (orderBooks.containsKey(marketCode)) {
            return Optional.of(orderBooks.get(marketCode));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Market> getMarket(String id) {
        return Optional.ofNullable(markets.get(id));
    }

    private Order toModel(OrderEntity entity) {
        Order order = modelMapper.map(entity, Order.class);
        order.setTimestamp(entity.getCreatedAt().getTime());
        return order;
    }
}
