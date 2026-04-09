package com.futures.analysis.ai;

import com.futures.analysis.data.FuturesData;
import com.futures.analysis.data.NewsData;

import java.util.*;

/**
 * AI分析引擎实现类 - 基于大语言模型的期货分析
 */
public class AIBasedAnalysisEngine implements AIAnalysisEngine {
    
    // 学习模型数据存储
    private Map<String, List<PredictionRecord>> learningRecords;
    
    public AIBasedAnalysisEngine() {
        this.learningRecords = new HashMap<>();
    }
    
    @Override
    public AnalysisResult performTechnicalAnalysis(String symbol, List<FuturesData> historicalData, Map<String, Object> technicalIndicators) {
        StringBuilder conclusion = new StringBuilder();
        StringBuilder recommendation = new StringBuilder();
        double confidence = 0.0;
        
        // 分析技术指标
        if (technicalIndicators.containsKey("MA5") && technicalIndicators.containsKey("MA10") && technicalIndicators.containsKey("MA20")) {
            double ma5 = (Double) technicalIndicators.get("MA5");
            double ma10 = (Double) technicalIndicators.get("MA10");
            double ma20 = (Double) technicalIndicators.get("MA20");
            
            // 均线排列分析
            if (ma5 > ma10 && ma10 > ma20) {
                conclusion.append("均线呈多头排列，短期趋势看涨");
                recommendation.append("建议逢低做多");
                confidence = 0.75;
            } else if (ma5 < ma10 && ma10 < ma20) {
                conclusion.append("均线呈空头排列，短期趋势看跌");
                recommendation.append("建议逢高做空");
                confidence = 0.75;
            } else {
                conclusion.append("均线排列混乱，趋势不明朗");
                recommendation.append("建议观望或轻仓操作");
                confidence = 0.5;
            }
        }
        
        // RSI分析
        if (technicalIndicators.containsKey("RSI")) {
            double rsi = (Double) technicalIndicators.get("RSI");
            
            if (rsi > 70) {
                conclusion.append("，RSI超买，注意回调风险");
                if (recommendation.toString().contains("做多")) {
                    recommendation.append("，适当减仓");
                }
                confidence = Math.min(confidence, 0.65);
            } else if (rsi < 30) {
                conclusion.append("，RSI超卖，存在反弹机会");
                if (recommendation.toString().contains("做空")) {
                    recommendation.append("，考虑平空单");
                }
                confidence = Math.min(confidence, 0.65);
            }
        }
        
        // MACD分析
        if (technicalIndicators.containsKey("MACD") && technicalIndicators.containsKey("DIF") && technicalIndicators.containsKey("DEA")) {
            double macd = (Double) technicalIndicators.get("MACD");
            double dif = (Double) technicalIndicators.get("DIF");
            double dea = (Double) technicalIndicators.get("DEA");
            
            if (dif > dea && macd > 0) {
                conclusion.append("，MACD金叉，多头动能增强");
                if (!recommendation.toString().contains("做多")) {
                    recommendation = new StringBuilder("建议关注做多机会");
                }
                confidence = Math.max(confidence, 0.7);
            } else if (dif < dea && macd < 0) {
                conclusion.append("，MACD死叉，空头动能增强");
                if (!recommendation.toString().contains("做空")) {
                    recommendation = new StringBuilder("建议关注做空机会");
                }
                confidence = Math.max(confidence, 0.7);
            }
        }
        
        // 布林带分析
        if (technicalIndicators.containsKey("BOLL_UPPER") && technicalIndicators.containsKey("BOLL_MIDDLE") && technicalIndicators.containsKey("BOLL_LOWER")) {
            double upper = (Double) technicalIndicators.get("BOLL_UPPER");
            double middle = (Double) technicalIndicators.get("BOLL_MIDDLE");
            double lower = (Double) technicalIndicators.get("BOLL_LOWER");
            
            if (!historicalData.isEmpty()) {
                double currentPrice = historicalData.get(historicalData.size() - 1).getClosePrice();
                
                if (currentPrice > upper) {
                    conclusion.append("，价格触及布林带上轨，短期有回调压力");
                    if (recommendation.toString().contains("做多")) {
                        recommendation.append("，注意止盈");
                    }
                    confidence = Math.min(confidence, 0.7);
                } else if (currentPrice < lower) {
                    conclusion.append("，价格触及布林带下轨，短期有反弹支撑");
                    if (recommendation.toString().contains("做空")) {
                        recommendation.append("，注意止损");
                    }
                    confidence = Math.min(confidence, 0.7);
                }
            }
        }
        
        // 构建详细分析数据
        Map<String, Object> details = new HashMap<>();
        details.putAll(technicalIndicators);
        details.put("latestPrice", historicalData.isEmpty() ? 0 : historicalData.get(historicalData.size() - 1).getClosePrice());
        
        return new AnalysisResult(
            symbol,
            "技术分析",
            conclusion.toString(),
            recommendation.toString(),
            confidence,
            details,
            new Date()
        );
    }
    
    @Override
    public AnalysisResult performFundamentalAnalysis(String symbol, List<NewsData> newsData, Map<String, Object> marketData) {
        StringBuilder conclusion = new StringBuilder();
        StringBuilder recommendation = new StringBuilder();
        double confidence = 0.0;
        
        int positiveCount = 0;
        int negativeCount = 0;
        int neutralCount = 0;
        
        // 分析新闻情感倾向
        for (NewsData news : newsData) {
            String content = news.getContent().toLowerCase();
            
            // 简单的情感词匹配（实际应用中应使用更复杂的情感分析模型）
            if (isPositiveSentiment(content)) {
                positiveCount++;
            } else if (isNegativeSentiment(content)) {
                negativeCount++;
            } else {
                neutralCount++;
            }
        }
        
        if (positiveCount > negativeCount) {
            conclusion.append("基本面偏多，利好消息较多");
            recommendation.append("偏向多头操作");
            confidence = 0.6 + (positiveCount - negativeCount) * 0.1;
        } else if (negativeCount > positiveCount) {
            conclusion.append("基本面偏空，利空消息较多");
            recommendation.append("偏向空头操作");
            confidence = 0.6 + (negativeCount - positiveCount) * 0.1;
        } else {
            conclusion.append("基本面中性，多空消息均衡");
            recommendation.append("谨慎操作，控制仓位");
            confidence = 0.5;
        }
        
        // 根据期货品种特性添加特定分析
        if (symbol.toLowerCase().contains("cu") || symbol.toLowerCase().contains("al") || 
            symbol.toLowerCase().contains("zn") || symbol.toLowerCase().contains("ni")) {
            // 有色金属分析
            conclusion.append("，受全球经济形势和供需关系影响较大");
        } else if (symbol.toLowerCase().contains("a") || symbol.toLowerCase().contains("m") || 
                  symbol.toLowerCase().contains("y") || symbol.toLowerCase().contains("c")) {
            // 农产品分析
            conclusion.append("，关注天气变化和季节性因素");
        } else if (symbol.toLowerCase().contains("cl") || symbol.toLowerCase().contains("sc")) {
            // 能源分析
            conclusion.append("，受地缘政治和OPEC政策影响显著");
        }
        
        // 限制置信度在合理范围内
        confidence = Math.min(0.9, Math.max(0.3, confidence));
        
        // 构建详细分析数据
        Map<String, Object> details = new HashMap<>();
        details.put("positiveNewsCount", positiveCount);
        details.put("negativeNewsCount", negativeCount);
        details.put("neutralNewsCount", neutralCount);
        details.put("totalNewsCount", newsData.size());
        details.put("marketData", marketData);
        
        return new AnalysisResult(
            symbol,
            "基本面分析",
            conclusion.toString(),
            recommendation.toString(),
            confidence,
            details,
            new Date()
        );
    }
    
    @Override
    public TrendPredictionResult predictTrend(String symbol, AnalysisResult technicalAnalysisResult, 
                                             AnalysisResult fundamentalAnalysisResult, List<FuturesData> historicalData) {
        String trendDirection = "震荡";
        double probability = 0.5;
        double targetPrice = 0.0;
        String timeFrame = "短期";
        StringBuilder analysisReason = new StringBuilder();
        List<String> keyFactors = new ArrayList<>();
        Map<String, Object> supportingData = new HashMap<>();
        
        // 综合技术分析和基本面分析结果
        boolean techBullish = technicalAnalysisResult.getRecommendation().contains("多") || 
                             technicalAnalysisResult.getRecommendation().contains("买");
        boolean techBearish = technicalAnalysisResult.getRecommendation().contains("空") || 
                             technicalAnalysisResult.getRecommendation().contains("卖");
        
        boolean fundBullish = fundamentalAnalysisResult.getRecommendation().contains("多") || 
                             fundamentalAnalysisResult.getRecommendation().contains("买");
        boolean fundBearish = fundamentalAnalysisResult.getRecommendation().contains("空") || 
                             fundamentalAnalysisResult.getRecommendation().contains("卖");
        
        // 计算综合置信度
        double techConfidence = technicalAnalysisResult.getConfidence();
        double fundConfidence = fundamentalAnalysisResult.getConfidence();
        double combinedConfidence = (techConfidence + fundConfidence) / 2;
        
        // 确定趋势方向
        if (techBullish && fundBullish) {
            trendDirection = "上涨";
            probability = 0.7 + combinedConfidence * 0.2;
            keyFactors.add("技术面看涨");
            keyFactors.add("基本面偏多");
            analysisReason.append("技术面和基本面均显示看涨信号，上涨概率较高");
        } else if (techBearish && fundBearish) {
            trendDirection = "下跌";
            probability = 0.7 + combinedConfidence * 0.2;
            keyFactors.add("技术面看跌");
            keyFactors.add("基本面偏空");
            analysisReason.append("技术面和基本面均显示看跌信号，下跌概率较高");
        } else if (techBullish || fundBullish) {
            trendDirection = "上涨";
            probability = 0.5 + combinedConfidence * 0.3;
            if (techBullish) keyFactors.add("技术面看涨");
            if (fundBullish) keyFactors.add("基本面偏多");
            analysisReason.append("技术面或基本面显示一定看涨信号");
        } else if (techBearish || fundBearish) {
            trendDirection = "下跌";
            probability = 0.5 + combinedConfidence * 0.3;
            if (techBearish) keyFactors.add("技术面看跌");
            if (fundBearish) keyFactors.add("基本面偏空");
            analysisReason.append("技术面或基本面显示一定看跌信号");
        } else {
            probability = 0.4 + combinedConfidence * 0.2;
            keyFactors.add("技术面中性");
            keyFactors.add("基本面中性");
            analysisReason.append("技术面和基本面均显示震荡格局");
        }
        
        // 计算目标价格（基于历史数据和技术指标）
        if (!historicalData.isEmpty()) {
            FuturesData latestData = historicalData.get(historicalData.size() - 1);
            targetPrice = latestData.getClosePrice();
            
            // 基于趋势方向调整目标价格
            if ("上涨".equals(trendDirection)) {
                targetPrice *= (1 + probability * 0.02); // 上涨幅度与概率相关
            } else if ("下跌".equals(trendDirection)) {
                targetPrice *= (1 - probability * 0.02); // 下跌幅度与概率相关
            }
        }
        
        // 确定时间框架
        if (probability > 0.8) {
            timeFrame = "中期";
        } else if (probability > 0.6) {
            timeFrame = "短期";
        } else {
            timeFrame = "日内";
        }
        
        // 风险等级评估
        String riskLevel = "中";
        if (probability > 0.8) {
            riskLevel = "低";
        } else if (probability < 0.5) {
            riskLevel = "高";
        }
        
        // 入场点和出场点建议
        String entryPoint = "当前价位";
        String exitPoint = "止损位";
        
        if (!historicalData.isEmpty()) {
            FuturesData latestData = historicalData.get(historicalData.size() - 1);
            double currentPrice = latestData.getClosePrice();
            
            if ("上涨".equals(trendDirection)) {
                entryPoint = String.format("%.2f附近", currentPrice * 0.995); // 当前价略下方入场
                exitPoint = String.format("%.2f", currentPrice * 0.98); // 当前价下方设置止损
            } else if ("下跌".equals(trendDirection)) {
                entryPoint = String.format("%.2f附近", currentPrice * 1.005); // 当前价略上方入场
                exitPoint = String.format("%.2f", currentPrice * 1.02); // 当前价上方设置止损
            }
        }
        
        // 保存支撑数据
        supportingData.put("technicalAnalysis", technicalAnalysisResult);
        supportingData.put("fundamentalAnalysis", fundamentalAnalysisResult);
        supportingData.put("combinedConfidence", combinedConfidence);
        
        return new TrendPredictionResult(
            symbol,
            trendDirection,
            Math.min(0.95, probability), // 限制最高概率为95%
            targetPrice,
            timeFrame,
            analysisReason.toString(),
            keyFactors,
            supportingData,
            new Date(),
            riskLevel,
            entryPoint,
            exitPoint
        );
    }
    
    @Override
    public void updateLearningModel(String symbol, TrendPredictionResult prediction, FuturesData actual, String feedback) {
        // 创建预测记录
        PredictionRecord record = new PredictionRecord(
            symbol,
            prediction,
            actual,
            feedback,
            new Date()
        );
        
        // 将记录添加到学习数据中
        List<PredictionRecord> records = learningRecords.computeIfAbsent(symbol, k -> new ArrayList<>());
        records.add(record);
        
        // 如果记录数量超过阈值，则保留最新的记录以控制内存使用
        if (records.size() > 1000) {
            // 移除最早的记录
            records.subList(0, records.size() - 900).clear();
        }
    }
    
    /**
     * 检查文本是否包含正面情感词汇
     */
    private boolean isPositiveSentiment(String text) {
        String[] positiveWords = {
            "上涨", "利好", "增长", "复苏", "改善", "强劲", "乐观", "买入", "推荐", 
            "提升", "扩张", "需求", "供应紧张", "上涨空间", "积极", "看涨", "多头"
        };
        
        for (String word : positiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查文本是否包含负面情感词汇
     */
    private boolean isNegativeSentiment(String text) {
        String[] negativeWords = {
            "下跌", "利空", "衰退", "疲软", "下滑", "悲观", "卖出", "减持", 
            "下降", "收缩", "供应过剩", "下跌空间", "消极", "看跌", "空头", "风险"
        };
        
        for (String word : negativeWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 预测记录内部类，用于学习模型
     */
    private static class PredictionRecord {
        String symbol;
        TrendPredictionResult prediction;
        FuturesData actual;
        String feedback;
        Date recordTime;
        
        PredictionRecord(String symbol, TrendPredictionResult prediction, FuturesData actual, 
                        String feedback, Date recordTime) {
            this.symbol = symbol;
            this.prediction = prediction;
            this.actual = actual;
            this.feedback = feedback;
            this.recordTime = recordTime;
        }
    }
}