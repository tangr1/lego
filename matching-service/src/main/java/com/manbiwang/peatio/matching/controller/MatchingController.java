package com.manbiwang.peatio.matching.controller;

import com.manbiwang.peatio.matching.model.OrderRequest;
import com.manbiwang.peatio.matching.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tangrui on 8/17/17.
 */
@RestController
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @PostMapping("/orders")
    public void order(@RequestBody OrderRequest orderRequest) {
        matchingService.handleOrderRequest(orderRequest);
    }
}
