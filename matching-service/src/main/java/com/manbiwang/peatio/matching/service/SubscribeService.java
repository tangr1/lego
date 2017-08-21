package com.manbiwang.peatio.matching.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manbiwang.peatio.matching.model.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by tangrui on 8/17/17.
 */
@Service
@RabbitListener(queues = "${matching.rabbitmq.queue}")
public class SubscribeService {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeService.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MatchingService matchingService;

    @RabbitHandler
    public void process(@Payload byte[] payload) {
        try {
             OrderRequest orderRequest = objectMapper.readValue(payload, OrderRequest.class);
             matchingService.processOrderRequest(orderRequest);
        } catch (IOException e) {
            logger.error("解析json失败：{}", new String(payload), e);
        }
    }
}
