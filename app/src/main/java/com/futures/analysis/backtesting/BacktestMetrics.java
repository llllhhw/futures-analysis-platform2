package com.futures.analysis.backtesting;

import java.util.Map;

/**
 * 回测绩效指标类
 */
class BacktestMetrics {
    private double totalReturn;           // 总收益率
    private double sharpeRatio;           // 夏普比率
    private double maxDrawdown;           // 最大回撤
    private double winRate;               // 胜率
    private Map<String, Object> additionalMetrics; // 额外指标
    
    public BacktestMetrics(double totalReturn, double sharpeRatio, double maxDrawdown, 
                          double winRate, Map<String, Object> additionalMetrics) {
        this.totalReturn = totalReturn;
        this.sharpeRatio = sharpeRatio;
        this.maxDrawdown = maxDrawdown;
        this.winRate = winRate;
        this.additionalMetrics = additionalMetrics;
    }
    
    // Getter和Setter方法
    public double getTotalReturn() { return totalReturn; }
    public void setTotalReturn(double totalReturn) { this.totalReturn = totalReturn; }
    
    public double getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(double sharpeRatio) { this.sharpeRatio = sharpeRatio; }
    
    public double getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
    
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    
    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
}