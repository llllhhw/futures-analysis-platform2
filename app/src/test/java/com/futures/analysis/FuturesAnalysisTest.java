package com.futures.analysis;

import com.futures.analysis.ai.AIBasedAnalysisEngine;
import com.futures.analysis.ai.AIAnalysisEngine;
import com.futures.analysis.ai.AnalysisResult;
import com.futures.analysis.ai.TrendPredictionResult;
import com.futures.analysis.backtesting.BacktestResult;
import com.futures.analysis.backtesting.BacktestingEngine;
import com.futures.analysis.data.FuturesData;
import com.futures.analysis.data.NewsData;
import com.futures.analysis.data.impl.MultiExchangeDataSource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 期货分析App功能验证测试类
 */
public class FuturesAnalysisTest {
    
    public static void main(String[] args) {
        System.out.println("开始测试期货分析App核心功能...");
        
        // 测试数据获取功能
        testDataFetching();
        
        // 测试AI分析功能
        testAIAnalysis();
        
        // 测试回测功能
        testBacktesting();
        
        System.out.println("期货分析App核心功能测试完成！");
    }
    
    /**
     * 测试数据获取功能
     */
    private static void testDataFetching() {
        System.out.println("\n--- 测试数据获取功能 ---");
        
        MultiExchangeDataSource dataSource = new MultiExchangeDataSource();
        
        // 测试获取支持的期货品种
        List<String> symbols = dataSource.getSupportedSymbols();
        System.out.println("支持的期货品种数量: " + symbols.size());
        System.out.println("前5个品种: " + symbols.subList(0, Math.min(5, symbols.size())));
        
        // 测试获取实时数据
        if (!symbols.isEmpty()) {
            String testSymbol = symbols.get(0);
            FuturesData realTimeData = dataSource.getRealTimeData(testSymbol);
            System.out.println("实时数据示例 (" + testSymbol + "): " + 
                             "价格=" + realTimeData.getClosePrice() + 
                             ", 涨跌幅=" + realTimeData.getChangePercent() + "%");
        }
        
        // 测试获取历史数据
        if (!symbols.isEmpty()) {
            String testSymbol = symbols.get(0);
            Date endDate = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
            Date startDate = cal.getTime();
            
            List<FuturesData> historicalData = dataSource.getHistoricalData(
                testSymbol,
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(startDate),
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(endDate),
                "daily"
            );
            
            System.out.println("历史数据示例 (" + testSymbol + "): " + 
                             "共" + historicalData.size() + "条记录");
            if (!historicalData.isEmpty()) {
                FuturesData latest = historicalData.get(historicalData.size() - 1);
                System.out.println("最新记录: " + latest.getClosePrice());
            }
        }
        
        // 测试获取市场新闻
        List<NewsData> news = dataSource.getMarketNews();
        System.out.println("市场新闻数量: " + news.size());
        if (!news.isEmpty()) {
            System.out.println("首条新闻: " + news.get(0).getTitle());
        }
        
        System.out.println("数据获取功能测试完成！");
    }
    
    /**
     * 测试AI分析功能
     */
    private static void testAIAnalysis() {
        System.out.println("\n--- 测试AI分析功能 ---");
        
        MultiExchangeDataSource dataSource = new MultiExchangeDataSource();
        AIAnalysisEngine aiEngine = new AIBasedAnalysisEngine();
        
        List<String> symbols = dataSource.getSupportedSymbols();
        if (!symbols.isEmpty()) {
            String testSymbol = symbols.get(0);
            
            // 获取历史数据和新闻用于分析
            Date endDate = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            Date startDate = cal.getTime();
            
            List<FuturesData> historicalData = dataSource.getHistoricalData(
                testSymbol,
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(startDate),
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(endDate),
                "daily"
            );
            
            List<NewsData> newsData = dataSource.getMarketNews();
            
            // 计算技术指标
            Map<String, Object> technicalIndicators = new HashMap<>();
            if (!historicalData.isEmpty()) {
                technicalIndicators = ((MultiExchangeDataSource) dataSource)
                    .getTechnicalIndicators(testSymbol, "daily", 30);
            }
            
            // 执行技术分析
            AnalysisResult techAnalysis = aiEngine.performTechnicalAnalysis(
                testSymbol, historicalData, technicalIndicators);
            
            System.out.println("技术分析结果: " + techAnalysis.getConclusion());
            System.out.println("技术分析置信度: " + techAnalysis.getConfidence());
            
            // 执行基本面分析
            AnalysisResult fundAnalysis = aiEngine.performFundamentalAnalysis(
                testSymbol, newsData, new HashMap<>());
            
            System.out.println("基本面分析结果: " + fundAnalysis.getConclusion());
            System.out.println("基本面分析置信度: " + fundAnalysis.getConfidence());
            
            // 综合预测
            TrendPredictionResult prediction = aiEngine.predictTrend(
                testSymbol, techAnalysis, fundAnalysis, historicalData);
            
            System.out.println("趋势预测: " + prediction.getTrendDirection());
            System.out.println("预测概率: " + prediction.getProbability());
            System.out.println("目标价格: " + prediction.getTargetPrice());
            System.out.println("分析理由: " + prediction.getAnalysisReason());
            
            System.out.println("AI分析功能测试完成！");
        }
    }
    
    /**
     * 测试回测功能
     */
    private static void testBacktesting() {
        System.out.println("\n--- 测试回测功能 ---");
        
        MultiExchangeDataSource dataSource = new MultiExchangeDataSource();
        BacktestingEngine backtestingEngine = new BacktestingEngine();
        
        List<String> symbols = dataSource.getSupportedSymbols();
        if (!symbols.isEmpty()) {
            String testSymbol = symbols.get(0);
            
            // 设置回测参数
            Date endDate = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -3); // 回测3个月数据
            Date startDate = cal.getTime();
            double initialCapital = 100000; // 初始资金10万
            
            // 创建数据提供者
            BacktestingEngine.DataProvider dataProvider = new BacktestingEngine.DataProvider() {
                @Override
                public List<FuturesData> getHistoricalData(String symbol, Date startDate, Date endDate) {
                    String startStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(startDate);
                    String endStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(endDate);
                    return dataSource.getHistoricalData(symbol, startStr, endStr, "daily");
                }

                @Override
                public List<NewsData> getNewsData(String symbol, Date startDate, Date endDate) {
                    return dataSource.getMarketNews();
                }
            };
            
            // 执行回测
            BacktestResult result = backtestingEngine.runBacktest(
                testSymbol, startDate, endDate, initialCapital, dataProvider);
            
            if (result.isSuccess()) {
                System.out.println("回测结果:");
                System.out.println("  总收益率: " + (result.getTotalReturn() * 100) + "%");
                System.out.println("  夏普比率: " + result.getSharpeRatio());
                System.out.println("  最大回撤: " + (result.getMaxDrawdown() * 100) + "%");
                System.out.println("  胜率: " + (result.getWinRate() * 100) + "%");
                System.out.println("  初始资金: " + result.getInitialCapital());
                
                if (result.getAdditionalMetrics() != null) {
                    System.out.println("  额外指标: " + result.getAdditionalMetrics());
                }
                
                System.out.println("  交易次数: " + result.getTransactions().size());
            } else {
                System.out.println("回测失败: " + result.getErrorMessage());
            }
            
            System.out.println("回测功能测试完成！");
        }
    }
}