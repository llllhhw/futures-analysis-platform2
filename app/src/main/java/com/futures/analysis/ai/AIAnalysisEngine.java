package com.futures.analysis.ai;

import com.futures.analysis.data.FuturesData;
import com.futures.analysis.data.NewsData;

import java.util.List;
import java.util.Map;

/**
 * AI分析引擎接口
 */
public interface AIAnalysisEngine {
    
    /**
     * 对期货进行技术分析
     * @param symbol 期货合约代码
     * @param historicalData 历史数据
     * @param technicalIndicators 技术指标
     * @return 技术分析结果
     */
    AnalysisResult performTechnicalAnalysis(String symbol, List<FuturesData> historicalData, Map<String, Object> technicalIndicators);
    
    /**
     * 对期货进行基本面分析
     * @param symbol 期货合约代码
     * @param newsData 相关新闻数据
     * @param marketData 市场数据
     * @return 基本面分析结果
     */
    AnalysisResult performFundamentalAnalysis(String symbol, List<NewsData> newsData, Map<String, Object> marketData);
    
    /**
     * 综合分析并预测趋势
     * @param symbol 期货合约代码
     * @param technicalAnalysisResult 技术分析结果
     * @param fundamentalAnalysisResult 基本面分析结果
     * @param historicalData 历史数据
     * @return 综合分析和趋势预测结果
     */
    TrendPredictionResult predictTrend(String symbol, AnalysisResult technicalAnalysisResult, 
                                      AnalysisResult fundamentalAnalysisResult, List<FuturesData> historicalData);
    
    /**
     * 更新学习模型
     * @param symbol 期货合约代码
     * @param prediction 预测结果
     * @param actual 实际结果
     * @param feedback 反馈信息
     */
    void updateLearningModel(String symbol, TrendPredictionResult prediction, FuturesData actual, String feedback);
}