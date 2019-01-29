package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.config.MatchingConfig;
import com.manbiwang.peatio.matching.entity.OrderEntity;
import com.manbiwang.peatio.matching.model.*;
import com.manbiwang.peatio.matching.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * Created by tangrui on 8/17/17.
 */
@Service
public class ProcessService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MatchingConfig matchingConfig;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PublishService publishService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private LimitOrderHandler limitOrderHandler;
    @Autowired
    private MarketOrderHandler marketOrderHandler;
    private Map<Integer, OrderBook> orderBooks;
    private Map<String, OrderHandler> handlers;
    private Map<String, Market> markets;
    private Map<Integer, String> marketCodes;

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
        handlers = new HashMap<>();
        handlers.put(OrderStrategy.LIMIT, limitOrderHandler);
        handlers.put(OrderStrategy.MARKET, marketOrderHandler);
        markets = matchingConfig.getMarkets().stream().collect(Collectors.toMap(Market::getId, e -> e));
        marketCodes = matchingConfig.getMarkets().stream().collect(Collectors.toMap(Market::getCode, Market::getId));
        matchingConfig.getMarkets().forEach(market ->
                orderRepository.findByCurrencyAndStateOrderById(market.getCode(), OrderState.WAIT)
                        .stream().map(this::toModel).forEach(this::submit));
    }

    public void processOrderRequest(OrderRequest orderRequest) {
        switch (orderRequest.getAction()) {
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

    private void doSubmit(Order order, OrderHandler orderHandler, Market market, OrderBook orderBook) {
        // 不断匹配订单做交易，直到数量耗尽
        while (!orderHandler.isFilled(order)) {
            Trade trade = new Trade(order, orderHandler, market, orderBook);
            // 寻找配对订单
            trade = tradeService.match(trade).orElse(null);
            // 找不到可配对的订单，马上退出循环
            if (trade == null || trade.getCounter() == null) {
                break;
            }
            // 找到配对订单，进行交易成功会修改订单和配对订单的数量金额
            trade.setCounterHandler(handlers.get(trade.getCounter().getStrategy()));
            if (trade.getCounterHandler() == null) {
                break;
            }
            tradeService.trade(trade).ifPresent(publishService::publishTrade);
        }
        tradeService.finish(order, orderHandler, orderBook);
    }

    private void submit(Order order) {
        Optional.ofNullable(handlers.get(order.getStrategy()))
                .ifPresent(orderHandler -> Optional.ofNullable(markets.get(order.getMarket()))
                        .ifPresent(market -> Optional.ofNullable(orderBooks.get(market.getCode()))
                                .ifPresent(orderBook -> doSubmit(order, orderHandler, market, orderBook))));
    }

    private void cancel(Order order) {
        Optional.ofNullable(handlers.get(order.getStrategy()))
                .ifPresent(orderHandler -> Optional.ofNullable(markets.get(order.getMarket()))
                        .ifPresent(market -> Optional.ofNullable(orderBooks.get(market.getCode()))
                                .ifPresent(orderBook -> tradeService.cancel(order, orderHandler, orderBook))));
    }

    private Order toModel(OrderEntity entity) {
        Order order = modelMapper.map(entity, Order.class);
        order.setMarket(marketCodes.get(entity.getCurrency()));
        order.setTimestamp(entity.getCreatedAt().getTime());
        return order;
    }
}
