package com.manbiwang.peatio.matching.service;

import com.manbiwang.peatio.matching.model.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by tangrui on 8/17/17.
 */
@Service
public class PublishService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void broadcast(Map<String, String> data) {

    }

    public void broadcastUpdate(Order order) {

    }

    public void broadcastRemove(Order order) {

    }

    public void publishTrade(Trade trade) {
    }

    public void publishAddOrder(Order order) {
    }

    public void publishCancelOrder(Order order) {
    }
}
