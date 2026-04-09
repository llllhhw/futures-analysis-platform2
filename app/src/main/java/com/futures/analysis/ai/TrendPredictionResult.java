package com.futures.analysis.ai;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 趋势预测结果类
 */
public class TrendPredictionResult {
    private String symbol;                           // 期货合约代码
    private String trendDirection;                   // 趋势方向（上涨、下跌、震荡）
    private double probability;                      // 预测概率（0-1）
    private double targetPrice;                      // 目标价格
    private String timeFrame;                        // 预测时间框架（短期、中期、长期）
    private String analysisReason;                   // 分析理由
    private List<String> keyFactors;                 // 关键影响因素
    private Map<String, Object> supportingData;      // 支撑数据
    private Date predictionTime;                     // 预测时间
    private String riskLevel;                        // 风险等级（高、中、低）
    private String entryPoint;                       // 入场点建议
    private String exitPoint;                        // 出场点建议
    
    // 构造函数
    public TrendPredictionResult() {}
    
    public TrendPredictionResult(String symbol, String trendDirection, double probability, 
                                double targetPrice, String timeFrame, String analysisReason, 
                                List<String> keyFactors, Map<String, Object> supportingData, 
                                Date predictionTime, String riskLevel, String entryPoint, String exitPoint) {
        this.symbol = symbol;
        this.trendDirection = trendDirection;
        this.probability = probability;
        this.targetPrice = targetPrice;
        this.timeFrame = timeFrame;
        this.analysisReason = analysisReason;
        this.keyFactors = keyFactors;
        this.supportingData = supportingData;
        this.predictionTime = predictionTime;
        this.riskLevel = riskLevel;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
    }
    
    // Getter和Setter方法
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getTrendDirection() { return trendDirection; }
    public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }
    
    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
    
    public double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(double targetPrice) { this.targetPrice = targetPrice; }
    
    public String getTimeFrame() { return timeFrame; }
    public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }
    
    public String getAnalysisReason() { return analysisReason; }
    public void setAnalysisReason(String analysisReason) { this.analysisReason = analysisReason; }
    
    public List<String> getKeyFactors() { return keyFactors; }
    public void setKeyFactors(List<String> keyFactors) { this.keyFactors = keyFactors; }
    
    public Map<String, Object> getSupportingData() { return supportingData; }
    public void setSupportingData(Map<String, Object> supportingData) { this.supportingData = supportingData; }
    
    public Date getPredictionTime() { return predictionTime; }
    public void setPredictionTime(Date predictionTime) { this.predictionTime = predictionTime; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getEntryPoint() { return entryPoint; }
    public void setEntryPoint(String entryPoint) { this.entryPoint = entryPoint; }
    
    public String getExitPoint() { return exitPoint; }
    public void setExitPoint(String exitPoint) { this.exitPoint = exitPoint; }
}