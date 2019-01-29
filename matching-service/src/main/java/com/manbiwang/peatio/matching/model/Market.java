package com.manbiwang.peatio.matching.model;

/**
 * Created by tangrui on 8/17/17.
 */
public class Market {

    private String id;
    private Integer code;
    private String name;
    private String baseUnit;
    private String quoteUnit;
    private Integer priceGroupFixed;
    private Double bidFee;
    private String bidCurrency;
    private Integer bidFixed;
    private Double askFee;
    private String askCurrency;
    private Integer askFixed;
    private Integer sortOrder;
    private Boolean visible;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getQuoteUnit() {
        return quoteUnit;
    }

    public void setQuoteUnit(String quoteUnit) {
        this.quoteUnit = quoteUnit;
    }

    public Integer getPriceGroupFixed() {
        return priceGroupFixed;
    }

    public void setPriceGroupFixed(Integer priceGroupFixed) {
        this.priceGroupFixed = priceGroupFixed;
    }

    public Double getBidFee() {
        return bidFee;
    }

    public void setBidFee(Double bidFee) {
        this.bidFee = bidFee;
    }

    public String getBidCurrency() {
        return bidCurrency;
    }

    public void setBidCurrency(String bidCurrency) {
        this.bidCurrency = bidCurrency;
    }

    public Integer getBidFixed() {
        return bidFixed;
    }

    public void setBidFixed(Integer bidFixed) {
        this.bidFixed = bidFixed;
    }

    public Double getAskFee() {
        return askFee;
    }

    public void setAskFee(Double askFee) {
        this.askFee = askFee;
    }

    public String getAskCurrency() {
        return askCurrency;
    }

    public void setAskCurrency(String askCurrency) {
        this.askCurrency = askCurrency;
    }

    public Integer getAskFixed() {
        return askFixed;
    }

    public void setAskFixed(Integer askFixed) {
        this.askFixed = askFixed;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
