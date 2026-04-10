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
    
    // 新增字段用于图表显示
    private java.util.List<Date> dates;              // 日期序列
    private java.util.List<Double> portfolioValues;  // 组合价值序列
    private java.util.List<Double> actualPrices;     // 实际价格序列
    private java.util.List<Double> predictedPrices;  // 预测价格序列（如果有）
    private java.util.List<Double> benchmarkReturns;// 基准收益序列（如持有不动）
    
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
    
    // 扩展构造函数以支持图表数据
    public BacktestResult(String symbol, double initialCapital, double totalReturn, 
                         double sharpeRatio, double maxDrawdown, double winRate,
                         java.util.List<Transaction> transactions, 
                         Map<String, Object> additionalMetrics, String errorMessage,
                         java.util.List<Date> dates, java.util.List<Double> portfolioValues,
                         java.util.List<Double> actualPrices, java.util.List<Double> predictedPrices,
                         java.util.List<Double> benchmarkReturns) {
        this(symbol, initialCapital, totalReturn, sharpeRatio, maxDrawdown, winRate, 
             transactions, additionalMetrics, errorMessage);
        this.dates = dates;
        this.portfolioValues = portfolioValues;
        this.actualPrices = actualPrices;
        this.predictedPrices = predictedPrices;
        this.benchmarkReturns = benchmarkReturns;
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
    
    // 图表数据的Getter和Setter方法
    public java.util.List<Date> getDates() { return dates; }
    public void setDates(java.util.List<Date> dates) { this.dates = dates; }
    
    public java.util.List<Double> getPortfolioValues() { return portfolioValues; }
    public void setPortfolioValues(java.util.List<Double> portfolioValues) { this.portfolioValues = portfolioValues; }
    
    public java.util.List<Double> getActualPrices() { return actualPrices; }
    public void setActualPrices(java.util.List<Double> actualPrices) { this.actualPrices = actualPrices; }
    
    public java.util.List<Double> getPredictedPrices() { return predictedPrices; }
    public void setPredictedPrices(java.util.List<Double> predictedPrices) { this.predictedPrices = predictedPrices; }
    
    public java.util.List<Double> getBenchmarkReturns() { return benchmarkReturns; }
    public void setBenchmarkReturns(java.util.List<Double> benchmarkReturns) { this.benchmarkReturns = benchmarkReturns; }
}