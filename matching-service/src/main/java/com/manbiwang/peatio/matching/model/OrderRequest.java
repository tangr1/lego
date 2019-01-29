package com.manbiwang.peatio.matching.model;

/**
 * Created by tangrui on 8/17/17.
 */
public class OrderRequest {

    private String action;
    private Order order;
    private String locale;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
