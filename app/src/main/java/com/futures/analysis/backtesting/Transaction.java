package com.futures.analysis.backtesting;

import java.util.Date;

/**
 * 交易记录类
 */
public class Transaction {
    private String symbol;                    // 期货合约代码
    private String type;                      // 交易类型 (BUY, SELL, SELL_SHORT, BUY_TO_COVER)
    private double quantity;                  // 交易数量
    private double price;                     // 交易价格
    private Date timestamp;                   // 交易时间
    private double preTransactionCapital;     // 交易前资金
    private double postTransactionCapital;    // 交易后资金
    private double preTransactionPosition;    // 交易前持仓
    private double postTransactionPosition;   // 交易后持仓
    private double positionPrice;             // 持仓均价
    private boolean isLong;                   // 是否多头持仓
    private double confidence;                // 预测置信度
    private String analysisReason;            // 分析理由
    
    public Transaction(String symbol, String type, double quantity, double price, Date timestamp,
                      double preTransactionCapital, double postTransactionCapital,
                      double preTransactionPosition, double postTransactionPosition,
                      double positionPrice, boolean isLong, double confidence, String analysisReason) {
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.preTransactionCapital = preTransactionCapital;
        this.postTransactionCapital = postTransactionCapital;
        this.preTransactionPosition = preTransactionPosition;
        this.postTransactionPosition = postTransactionPosition;
        this.positionPrice = positionPrice;
        this.isLong = isLong;
        this.confidence = confidence;
        this.analysisReason = analysisReason;
    }
    
    // Getter和Setter方法
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public double getPreTransactionCapital() { return preTransactionCapital; }
    public void setPreTransactionCapital(double preTransactionCapital) { this.preTransactionCapital = preTransactionCapital; }
    
    public double getPostTransactionCapital() { return postTransactionCapital; }
    public void setPostTransactionCapital(double postTransactionCapital) { this.postTransactionCapital = postTransactionCapital; }
    
    public double getPreTransactionPosition() { return preTransactionPosition; }
    public void setPreTransactionPosition(double preTransactionPosition) { this.preTransactionPosition = preTransactionPosition; }
    
    public double getPostTransactionPosition() { return postTransactionPosition; }
    public void setPostTransactionPosition(double postTransactionPosition) { this.postTransactionPosition = postTransactionPosition; }
    
    public double getPositionPrice() { return positionPrice; }
    public void setPositionPrice(double positionPrice) { this.positionPrice = positionPrice; }
    
    public boolean isLong() { return isLong; }
    public void setLong(boolean isLong) { this.isLong = isLong; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public String getAnalysisReason() { return analysisReason; }
    public void setAnalysisReason(String analysisReason) { this.analysisReason = analysisReason; }
}