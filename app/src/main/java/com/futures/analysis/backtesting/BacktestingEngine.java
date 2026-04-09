package com.futures.analysis.backtesting;

import com.futures.analysis.ai.AIAnalysisEngine;
import com.futures.analysis.ai.AIBasedAnalysisEngine;
import com.futures.analysis.ai.TrendPredictionResult;
import com.futures.analysis.data.FuturesData;
import com.futures.analysis.data.NewsData;

import java.util.*;

/**
 * 期货回测引擎
 */
public class BacktestingEngine {
    
    private AIAnalysisEngine aiEngine;
    
    public BacktestingEngine() {
        this.aiEngine = new AIBasedAnalysisEngine();
    }
    
    /**
     * 执行回测
     * @param symbol 期货合约代码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param initialCapital 初始资金
     * @param dataProvider 数据提供者
     * @return 回测结果
     */
    public BacktestResult runBacktest(String symbol, Date startDate, Date endDate, 
                                     double initialCapital, DataProvider dataProvider) {
        // 获取历史数据
        List<FuturesData> historicalData = dataProvider.getHistoricalData(symbol, startDate, endDate);
        List<NewsData> newsData = dataProvider.getNewsData(symbol, startDate, endDate);
        
        if (historicalData.isEmpty()) {
            return new BacktestResult(symbol, initialCapital, 0, 0, 0, 0, 
                                    new ArrayList<>(), new HashMap<>(), "无历史数据可回测");
        }
        
        // 初始化回测参数
        double capital = initialCapital;
        double position = 0; // 持仓量
        double positionPrice = 0; // 持仓均价
        boolean isLong = true; // 当前持仓方向(true为多头，false为空头)
        
        List<Transaction> transactions = new ArrayList<>();
        List<Double> portfolioValues = new ArrayList<>();
        
        // 遍历历史数据进行回测
        for (int i = 20; i < historicalData.size(); i++) { // 从第20个数据点开始（为了有足够的历史数据计算技术指标）
            FuturesData currentData = historicalData.get(i);
            Date currentDate = currentData.getTimestamp();
            
            // 获取当前时间点之前的数据用于分析
            List<FuturesData> pastData = historicalData.subList(Math.max(0, i-20), i);
            List<NewsData> relevantNews = getRelevantNews(newsData, currentDate);
            
            // 使用AI分析引擎进行分析
            Map<String, Object> technicalIndicators = calculateTechnicalIndicators(pastData);
            var technicalAnalysis = aiEngine.performTechnicalAnalysis(symbol, pastData, technicalIndicators);
            var fundamentalAnalysis = aiEngine.performFundamentalAnalysis(symbol, relevantNews, new HashMap<>());
            var prediction = aiEngine.predictTrend(symbol, technicalAnalysis, fundamentalAnalysis, pastData);
            
            // 根据预测结果决定交易操作
            Transaction transaction = makeTradingDecision(symbol, currentData, prediction, 
                                                        capital, position, positionPrice, isLong);
            
            if (transaction != null) {
                transactions.add(transaction);
                
                // 更新账户状态
                capital = transaction.getPostTransactionCapital();
                if (transaction.getType().equals("BUY") || transaction.getType().equals("SELL_SHORT")) {
                    position = transaction.getPostTransactionPosition();
                    positionPrice = transaction.getPositionPrice();
                    isLong = transaction.isLong();
                } else if (transaction.getType().equals("SELL") || transaction.getType().equals("BUY_TO_COVER")) {
                    position = 0;
                    positionPrice = 0;
                    isLong = true; // 平仓后重置持仓方向
                }
            }
            
            // 记录组合价值
            double currentValue = capital;
            if (position != 0) {
                if (isLong) {
                    currentValue += position * currentData.getClosePrice();
                } else {
                    // 空头持仓的价值计算（简化处理）
                    currentValue += position * (positionPrice - (currentData.getClosePrice() - positionPrice));
                }
            }
            portfolioValues.add(currentValue);
        }
        
        // 计算回测绩效指标
        BacktestMetrics metrics = calculateMetrics(initialCapital, portfolioValues, transactions);
        
        return new BacktestResult(symbol, initialCapital, metrics.getTotalReturn(), 
                                metrics.getSharpeRatio(), metrics.getMaxDrawdown(), 
                                metrics.getWinRate(), transactions, 
                                metrics.getAdditionalMetrics(), null);
    }
    
    /**
     * 获取相关时间段的新闻数据
     */
    private List<NewsData> getRelevantNews(List<NewsData> allNews, Date currentDate) {
        List<NewsData> relevantNews = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DAY_OF_MONTH, -7); // 获取过去一周的新闻
        Date startDate = cal.getTime();
        
        for (NewsData news : allNews) {
            if (news.getPublishTime().after(startDate) && news.getPublishTime().before(currentDate)) {
                relevantNews.add(news);
            }
        }
        
        return relevantNews;
    }
    
    /**
     * 计算技术指标
     */
    private Map<String, Object> calculateTechnicalIndicators(List<FuturesData> data) {
        Map<String, Object> indicators = new HashMap<>();
        
        if (data.size() < 20) {
            return indicators;
        }
        
        // 计算简单移动平均
        double sum5 = 0, sum10 = 0, sum20 = 0;
        int count5 = Math.min(5, data.size());
        int count10 = Math.min(10, data.size());
        int count20 = Math.min(20, data.size());
        
        for (int i = data.size() - count5; i < data.size(); i++) {
            sum5 += data.get(i).getClosePrice();
        }
        for (int i = data.size() - count10; i < data.size(); i++) {
            sum10 += data.get(i).getClosePrice();
        }
        for (int i = data.size() - count20; i < data.size(); i++) {
            sum20 += data.get(i).getClosePrice();
        }
        
        indicators.put("MA5", sum5 / count5);
        indicators.put("MA10", sum10 / count10);
        indicators.put("MA20", sum20 / count20);
        
        // 计算RSI (简化版)
        if (data.size() >= 14) {
            int gainCount = 0;
            int lossCount = 0;
            for (int i = data.size() - 14; i < data.size() - 1; i++) {
                double change = data.get(i+1).getClosePrice() - data.get(i).getClosePrice();
                if (change >= 0) {
                    gainCount++;
                } else {
                    lossCount++;
                }
            }
            double rsi = 100.0 - (100.0 / (1 + (double)gainCount/lossCount));
            indicators.put("RSI", rsi);
        }
        
        return indicators;
    }
    
    /**
     * 根据AI预测结果做出交易决策
     */
    private Transaction makeTradingDecision(String symbol, FuturesData currentData, 
                                          TrendPredictionResult prediction, 
                                          double capital, double position, 
                                          double positionPrice, boolean isLong) {
        String trendDirection = prediction.getTrendDirection();
        double confidence = prediction.getProbability();
        double currentPrice = currentData.getClosePrice();
        
        // 设置交易参数
        double minConfidence = 0.6; // 最小置信度
        double maxPositionPercent = 0.2; // 最大仓位比例
        double fixedRiskPercent = 0.02; // 固定风险比例（2%）
        
        if (confidence < minConfidence) {
            return null; // 置信度不足，不交易
        }
        
        String decision = null;
        double tradeAmount = 0;
        
        if ("上涨".equals(trendDirection) && !isLong) {
            // 空头转多头
            decision = "BUY_TO_COVER"; // 先平空仓
            tradeAmount = Math.abs(position);
        } else if ("上涨".equals(trendDirection) && isLong) {
            // 已持多头，考虑加仓
            if (capital > 0 && position * currentPrice < capital * maxPositionPercent) {
                decision = "BUY";
                tradeAmount = (capital * maxPositionPercent - position * currentPrice) / currentPrice * 0.8; // 留20%余地
            }
        } else if ("下跌".equals(trendDirection) && isLong) {
            // 多头转空头
            decision = "SELL"; // 先平多仓
            tradeAmount = Math.abs(position);
        } else if ("下跌".equals(trendDirection) && !isLong) {
            // 已持空头，考虑加仓
            if (capital > 0 && Math.abs(position) * currentPrice < capital * maxPositionPercent) {
                decision = "SELL_SHORT";
                tradeAmount = (capital * maxPositionPercent - Math.abs(position) * currentPrice) / currentPrice * 0.8; // 留20%余地
            }
        }
        
        if (decision != null) {
            // 计算交易后的账户状态
            double postCapital, postPosition, newPositionPrice;
            boolean newPosLong = isLong;
            
            switch(decision) {
                case "BUY":
                    tradeAmount = Math.min(tradeAmount, capital / currentPrice * 0.95); // 留5%现金应对手续费
                    postCapital = capital - tradeAmount * currentPrice;
                    postPosition = position + tradeAmount;
                    newPositionPrice = (position * positionPrice + tradeAmount * currentPrice) / postPosition;
                    newPosLong = true;
                    break;
                    
                case "SELL":
                    postCapital = capital + Math.min(position, tradeAmount) * currentPrice;
                    postPosition = position - Math.min(position, tradeAmount);
                    newPositionPrice = postPosition > 0 ? positionPrice : 0;
                    newPosLong = postPosition > 0;
                    break;
                    
                case "SELL_SHORT":
                    tradeAmount = Math.min(tradeAmount, capital / currentPrice * 0.95);
                    postCapital = capital + tradeAmount * currentPrice; // 卖空获得资金
                    postPosition = position - tradeAmount;
                    newPositionPrice = (Math.abs(position) * positionPrice + tradeAmount * currentPrice) / Math.abs(postPosition);
                    newPosLong = false;
                    break;
                    
                case "BUY_TO_COVER":
                    double amountToCover = Math.min(Math.abs(position), tradeAmount);
                    postCapital = capital - amountToCover * currentPrice + Math.abs(position) * positionPrice; // 平空仓
                    postPosition = position + amountToCover;
                    newPositionPrice = postPosition != 0 ? positionPrice : 0;
                    newPosLong = postPosition > 0;
                    break;
                    
                default:
                    return null;
            }
            
            return new Transaction(
                symbol,
                decision,
                tradeAmount,
                currentPrice,
                new Date(),
                capital,
                postCapital,
                position,
                postPosition,
                newPositionPrice,
                newPosLong,
                confidence,
                prediction.getAnalysisReason()
            );
        }
        
        return null;
    }
    
    /**
     * 计算回测绩效指标
     */
    private BacktestMetrics calculateMetrics(double initialCapital, List<Double> portfolioValues, List<Transaction> transactions) {
        if (portfolioValues.size() < 2) {
            return new BacktestMetrics(0, 0, 0, 0, new HashMap<>());
        }
        
        double finalCapital = portfolioValues.get(portfolioValues.size() - 1);
        double totalReturn = (finalCapital - initialCapital) / initialCapital;
        
        // 计算收益率序列
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < portfolioValues.size(); i++) {
            double ret = (portfolioValues.get(i) - portfolioValues.get(i-1)) / portfolioValues.get(i-1);
            returns.add(ret);
        }
        
        // 计算夏普比率（假设无风险利率为0.03/252每天）
        double riskFreeRate = 0.03 / 252;
        double excessReturn = 0;
        for (double ret : returns) {
            excessReturn += (ret - riskFreeRate);
        }
        excessReturn /= returns.size();
        
        double variance = 0;
        for (double ret : returns) {
            variance += Math.pow(ret - excessReturn - riskFreeRate, 2);
        }
        variance /= returns.size();
        double volatility = Math.sqrt(variance) * Math.sqrt(252); // 年化波动率
        double sharpeRatio = volatility != 0 ? (excessReturn * 252) / volatility : 0; // 年化夏普比率
        
        // 计算最大回撤
        double maxDrawdown = 0;
        double peak = portfolioValues.get(0);
        for (double value : portfolioValues) {
            if (value > peak) {
                peak = value;
            }
            double drawdown = (peak - value) / peak;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        
        // 计算胜率
        int winCount = 0;
        int totalCount = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("SELL") || transaction.getType().equals("BUY_TO_COVER")) {
                // 这是平仓操作，计算盈亏
                double entryPrice = transaction.getPositionPrice(); // 这里简化处理
                double exitPrice = transaction.getPrice();
                if ((transaction.isLong() && exitPrice > entryPrice) || 
                    (!transaction.isLong() && exitPrice < entryPrice)) {
                    winCount++;
                }
                totalCount++;
            }
        }
        double winRate = totalCount > 0 ? (double)winCount / totalCount : 0;
        
        // 计算其他指标
        Map<String, Object> additionalMetrics = new HashMap<>();
        additionalMetrics.put("totalTrades", transactions.size());
        additionalMetrics.put("profitableTrades", winCount);
        additionalMetrics.put("totalProfit", finalCapital - initialCapital);
        additionalMetrics.put("maxGain", Collections.max(portfolioValues) - initialCapital);
        additionalMetrics.put("maxLoss", Collections.min(portfolioValues) - initialCapital);
        
        return new BacktestMetrics(totalReturn, sharpeRatio, maxDrawdown, winRate, additionalMetrics);
    }
    
    /**
     * 数据提供者接口
     */
    public interface DataProvider {
        List<FuturesData> getHistoricalData(String symbol, Date startDate, Date endDate);
        List<NewsData> getNewsData(String symbol, Date startDate, Date endDate);
    }
}