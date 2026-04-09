package com.futures.analysis.backtesting;

import java.util.Date;
import java.util.Map;

/**
 * 回测结果类
 */
public class BacktestResult {
    private String symbol;                           // 期货合约代码
    private double initialCapital;                   // 初始资金
    private double totalReturn;                      // 总收益率
    private double sharpeRatio;                      // 夏普比率
    private double maxDrawdown;                      // 最大回撤
    private double winRate;                          // 胜率
    private java.util.List<Transaction> transactions; // 交易记录
    private Map<String, Object> additionalMetrics;   // 额外指标
    private String errorMessage;                     // 错误信息（如果有的话）
    
    public BacktestResult(String symbol, double initialCapital, double totalReturn, 
                         double sharpeRatio, double maxDrawdown, double winRate,
                         java.util.List<Transaction> transactions, 
                         Map<String, Object> additionalMetrics, String errorMessage) {
        this.symbol = symbol;
        this.initialCapital = initialCapital;
        this.totalReturn = totalReturn;
        this.sharpeRatio = sharpeRatio;
        this.maxDrawdown = maxDrawdown;
        this.winRate = winRate;
        this.transactions = transactions;
        this.additionalMetrics = additionalMetrics;
        this.errorMessage = errorMessage;
    }
    
    // Getter和Setter方法
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public double getInitialCapital() { return initialCapital; }
    public void setInitialCapital(double initialCapital) { this.initialCapital = initialCapital; }
    
    public double getTotalReturn() { return totalReturn; }
    public void setTotalReturn(double totalReturn) { this.totalReturn = totalReturn; }
    
    public double getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(double sharpeRatio) { this.sharpeRatio = sharpeRatio; }
    
    public double getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
    
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    
    public java.util.List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(java.util.List<Transaction> transactions) { this.transactions = transactions; }
    
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public boolean isSuccess() { return errorMessage == null; }
}