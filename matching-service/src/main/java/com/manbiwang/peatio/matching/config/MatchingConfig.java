package com.manbiwang.peatio.matching.config;

import com.manbiwang.peatio.matching.model.Market;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by tangrui on 8/17/17.
 */
@Configuration
@ConfigurationProperties(prefix = "matching")
public class MatchingConfig {

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }

    private List<Market> markets;
}