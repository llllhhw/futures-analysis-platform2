package com.futures.analysis.ai;

import java.util.Date;
import java.util.Map;

/**
 * 分析结果类
 */
public class AnalysisResult {
    private String symbol;              // 期货合约代码
    private String analysisType;        // 分析类型（技术分析/基本面分析）
    private String conclusion;          // 分析结论
    private String recommendation;      // 操作建议
    private double confidence;          // 置信度（0-1）
    private Map<String, Object> details; // 详细分析数据
    private Date timestamp;             // 分析时间
    
    // 构造函数
    public AnalysisResult() {}
    
    public AnalysisResult(String symbol, String analysisType, String conclusion, 
                         String recommendation, double confidence, 
                         Map<String, Object> details, Date timestamp) {
        this.symbol = symbol;
        this.analysisType = analysisType;
        this.conclusion = conclusion;
        this.recommendation = recommendation;
        this.confidence = confidence;
        this.details = details;
        this.timestamp = timestamp;
    }
    
    // Getter和Setter方法
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}