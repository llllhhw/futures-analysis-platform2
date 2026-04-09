package com.futures.analysis.data;

import java.util.Date;

/**
 * 期货数据实体类
 */
public class FuturesData {
    private String symbol;        // 期货合约代码
    private double openPrice;     // 开盘价
    private double highPrice;     // 最高价
    private double lowPrice;      // 最低价
    private double closePrice;    // 收盘价/当前价
    private double volume;        // 成交量
    private double openInterest;  // 持仓量
    private double change;        // 涨跌额
    private double changePercent; // 涨跌幅
    private Date timestamp;       // 时间戳
    private double preSettlement; // 前结算价
    
    // 构造函数
    public FuturesData() {}
    
    public FuturesData(String symbol, double openPrice, double highPrice, double lowPrice, 
                      double closePrice, double volume, double openInterest, double change, 
                      double changePercent, Date timestamp, double preSettlement) {
        this.symbol = symbol;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.openInterest = openInterest;
        this.change = change;
        this.changePercent = changePercent;
        this.timestamp = timestamp;
        this.preSettlement = preSettlement;
    }
    
    // Getter和Setter方法
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public double getOpenPrice() { return openPrice; }
    public void setOpenPrice(double openPrice) { this.openPrice = openPrice; }
    
    public double getHighPrice() { return highPrice; }
    public void setHighPrice(double highPrice) { this.highPrice = highPrice; }
    
    public double getLowPrice() { return lowPrice; }
    public void setLowPrice(double lowPrice) { this.lowPrice = lowPrice; }
    
    public double getClosePrice() { return closePrice; }
    public void setClosePrice(double closePrice) { this.closePrice = closePrice; }
    
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    public double getOpenInterest() { return openInterest; }
    public void setOpenInterest(double openInterest) { this.openInterest = openInterest; }
    
    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }
    
    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public double getPreSettlement() { return preSettlement; }
    public void setPreSettlement(double preSettlement) { this.preSettlement = preSettlement; }
}