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
                                    new ArrayList<>(), new HashMap<>(), "无历史数据可回测",
                                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
                                    new ArrayList<>(), new ArrayList<>());
        }
        
        // 初始化回测参数
        double capital = initialCapital;
        double position = 0; // 持仓量
        double positionPrice = 0; // 持仓均价
        boolean isLong = true; // 当前持仓方向(true为多头，false为空头)
        
        List<Transaction> transactions = new ArrayList<>();
        List<Double> portfolioValues = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        List<Double> actualPrices = new ArrayList<>();
        List<Double> predictedPrices = new ArrayList<>(); // 存储每日预测价格
        List<Double> benchmarkReturns = new ArrayList<>(); // 基准收益（买入持有）
        
        double initialPrice = historicalData.get(0).getClosePrice(); // 用于基准比较
        
        // 遍历历史数据进行回测
        for (int i = 20; i < historicalData.size(); i++) { // 从第20个数据点开始（为了有足够的历史数据计算技术指标）
            FuturesData currentData = historicalData.get(i);
            Date currentDate = currentData.getTimestamp();
            
            // 获取当前时间点之前的数据用于分析
            List<FuturesData> pastData = historicalData.subList(Math.max(0, i-20), i);
            List<NewsData> relevantNews = getRelevantNews(newsData, currentDate);
            
            // 使用AI分析引擎进行分析 - 确保有足够的数据进行分析
            Map<String, Object> technicalIndicators = calculateTechnicalIndicators(pastData);
            com.futures.analysis.ai.AnalysisResult technicalAnalysis = aiEngine.performTechnicalAnalysis(symbol, pastData, technicalIndicators);
            com.futures.analysis.ai.AnalysisResult fundamentalAnalysis = aiEngine.performFundamentalAnalysis(symbol, relevantNews, new HashMap<>());
            TrendPredictionResult prediction = aiEngine.predictTrend(symbol, technicalAnalysis, fundamentalAnalysis, pastData);
            
            // 输出调试信息，确保AI分析正在工作
            System.out.println("回测日期: " + currentDate + ", AI预测: " + prediction.getTrendDirection() + 
                              ", 概率: " + String.format("%.2f", prediction.getProbability()) + 
                              ", 目标价格: " + String.format("%.2f", prediction.getTargetPrice()));
            
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
        
                    // 记录组合价值（每天都要记录，即使没有交易）
                    double currentValue = capital;
                    if (position != 0) {
                        if (isLong) {
                            currentValue += position * currentData.getClosePrice();
                        } else {
                            // 空头持仓的价值计算
                            // 空头盈利 = 初始卖空价格 - 当前价格，所以价值 = 初始资金 + (初始价格 - 当前价格) * 数量
                            currentValue += Math.abs(position) * (2 * positionPrice - currentData.getClosePrice());
                        }
                    }
                    portfolioValues.add(currentValue);
                    dates.add(currentDate);
                    actualPrices.add(currentData.getClosePrice());
        
                    // 记录预测价格（使用趋势预测的目标价格）
                                if (prediction != null) {
                                    // 将预测价格相对于初始价格进行标准化，以便在图表中更好地展示
                                    double normalizedPredictedPrice = (prediction.getTargetPrice() / initialPrice) * initialCapital;
                                    predictedPrices.add(normalizedPredictedPrice);
                                } else {
                                    // 如果没有预测结果，使用当前价格作为占位符
                                    double normalizedCurrentPrice = (currentData.getClosePrice() / initialPrice) * initialCapital;
                                    predictedPrices.add(normalizedCurrentPrice);
                                }
        
                    // 计算基准收益（买入持有策略）
                    double currentBenchmarkValue = (initialCapital / initialPrice) * currentData.getClosePrice();
                    benchmarkReturns.add(currentBenchmarkValue);
        
                    System.out.println("日期: " + currentDate + ", 价格: " + currentData.getClosePrice() + 
                                      ", 持仓: " + position + ", 现金: " + capital + 
                                      ", 组合价值: " + currentValue + ", AI预测: " + 
                                      (prediction != null ? prediction.getTrendDirection() : "无"));
        }
        
        // 计算回测绩效指标
        BacktestMetrics metrics = calculateMetrics(initialCapital, portfolioValues, transactions, benchmarkReturns);
        
        // 创建包含图表数据的回测结果
        return new BacktestResult(
            symbol, 
            initialCapital, 
            metrics.getTotalReturn(), 
            metrics.getSharpeRatio(), 
            metrics.getMaxDrawdown(), 
            metrics.getWinRate(), 
            transactions, 
            metrics.getAdditionalMetrics(), 
            null, // errorMessage
            dates,
            portfolioValues,
            actualPrices,
            predictedPrices,
            benchmarkReturns
        );
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
            double prevPrice = data.get(data.size() - 14).getClosePrice();
            for (int i = data.size() - 14 + 1; i < data.size(); i++) {
                double currentPrice = data.get(i).getClosePrice();
                if (currentPrice > prevPrice) {
                    gainCount++;
                } else if (currentPrice < prevPrice) {
                    lossCount++;
                }
                prevPrice = currentPrice;
            }
            
            double rs = lossCount == 0 ? 100 : (double) gainCount / lossCount;
            double rsi = 100 - (100 / (1 + rs));
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
        double minConfidence = 0.65; // 提高最小置信度，减少随机交易
        double maxPositionPercent = 0.15; // 最大仓位比例
        
        // 只有在置信度足够高时才交易
        if (confidence < minConfidence) {
            return null; // 置信度不足，不交易
        }
        
        String decision = null;
        double tradeAmount = 0;
        double targetPositionSize = (capital * maxPositionPercent) / currentPrice;
        
        // 基于AI预测结果做出交易决策
        if ("上涨".equals(trendDirection)) {
            // AI预测价格上涨
            if (position == 0) {
                // 当前无持仓，开多头
                decision = "BUY";
                tradeAmount = Math.min(targetPositionSize, (capital * 0.1) / currentPrice);
            } else if (isLong) {
                // 当前持多头，考虑加仓
                if (position < targetPositionSize * 0.8) {
                    decision = "BUY";
                    tradeAmount = Math.min(targetPositionSize - position, (capital * 0.1) / currentPrice);
                }
            } else {
                // 当前持空头，先平空再开多
                decision = "BUY_TO_COVER";
                tradeAmount = Math.abs(position);
            }
        } else if ("下跌".equals(trendDirection)) {
            // AI预测价格下跌
            if (position == 0) {
                // 当前无持仓，开空头
                decision = "SELL_SHORT";
                tradeAmount = Math.min(targetPositionSize, (capital * 0.1) / currentPrice);
            } else if (!isLong) {
                // 当前持空头，考虑加仓
                if (Math.abs(position) < targetPositionSize * 0.8) {
                    decision = "SELL_SHORT";
                    tradeAmount = Math.min(targetPositionSize - Math.abs(position), (capital * 0.1) / currentPrice);
                }
            } else {
                // 当前持多头，先平多再开空
                decision = "SELL";
                tradeAmount = position;
            }
        } else {
            // 震荡或其他方向不明的情况，不进行新交易，仅考虑平仓
            // 检查是否需要止损或止盈
            if (position != 0) {
                double priceChangePercent = Math.abs((currentPrice - positionPrice) / positionPrice);
                
                // 设置止损/止盈条件
                if (priceChangePercent > 0.03) { // 3%止损/止盈
                    if (isLong) {
                        if ((currentPrice - positionPrice) / positionPrice < -0.03) {
                            // 多头亏损超过3%，止损
                            decision = "SELL";
                            tradeAmount = position;
                        } else if ((currentPrice - positionPrice) / positionPrice > 0.03) {
                            // 多头盈利超过3%，止盈
                            decision = "SELL";
                            tradeAmount = position;
                        }
                    } else {
                        if ((positionPrice - currentPrice) / positionPrice < -0.03) {
                            // 空头亏损超过3%，止损
                            decision = "BUY_TO_COVER";
                            tradeAmount = Math.abs(position);
                        } else if ((positionPrice - currentPrice) / positionPrice > 0.03) {
                            // 空头盈利超过3%，止盈
                            decision = "BUY_TO_COVER";
                            tradeAmount = Math.abs(position);
                        }
                    }
                }
            }
        }
        
        // 检查是否有足够的资金进行交易
        if (decision != null) {
            if (("BUY".equals(decision) || "SELL_SHORT".equals(decision)) && 
                capital < tradeAmount * currentPrice * 1.001) { // 加上手续费
                return null; // 资金不足
            }
        }
        
        if (decision != null) {
            // 计算交易后的账户状态
            double postCapital = capital;
            double postPosition = position;
            double newPositionPrice = positionPrice;
            boolean newPosLong = isLong;
            
            switch(decision) {
                case "BUY":
                    tradeAmount = Math.min(tradeAmount, capital / (currentPrice * 1.001)); // 考虑手续费
                    postCapital = capital - tradeAmount * currentPrice * 1.001;
                    postPosition = position + tradeAmount;
                    // 更新持仓均价：(原持仓价值 + 新买入价值) / 总持仓量
                    if (postPosition != 0) {
                        newPositionPrice = (position * positionPrice + tradeAmount * currentPrice) / postPosition;
                    }
                    newPosLong = true;
                    break;
                    
                case "SELL":
                    double sellAmount = Math.min(position, tradeAmount);
                    postCapital = capital + sellAmount * currentPrice * 0.999; // 考虑手续费
                    postPosition = position - sellAmount;
                    newPositionPrice = postPosition > 0 ? positionPrice : 0;
                    newPosLong = postPosition > 0;
                    break;
                    
                case "SELL_SHORT":
                    tradeAmount = Math.min(tradeAmount, capital / (currentPrice * 1.001)); // 确保有足够的保证金
                    postCapital = capital + tradeAmount * currentPrice * 0.999; // 卖空获得资金
                    postPosition = position - tradeAmount; // 空头持仓为负数
                    // 更新空头持仓均价：(原持仓绝对值价值 + 新卖空价值) / 总空头持仓绝对值
                    if (postPosition != 0) {
                        newPositionPrice = (Math.abs(position) * positionPrice + tradeAmount * currentPrice) / Math.abs(postPosition);
                    }
                    newPosLong = false;
                    break;
                    
                case "BUY_TO_COVER":
                    double coverAmount = Math.min(Math.abs(position), tradeAmount);
                    // 平空仓的成本 = 买回的数量 * 当前价格 * 1.001(手续费)
                    double coverCost = coverAmount * currentPrice * 1.001;
                    // 平空仓的盈亏 = (卖空价格 - 平仓价格) * 数量
                    double profit = (positionPrice - currentPrice) * coverAmount;
                    postCapital = capital - coverCost + profit;
                    postPosition = position + coverAmount; // 空头仓位减少
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
                currentData.getTimestamp(), // 使用数据的实际时间戳
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
    private BacktestMetrics calculateMetrics(double initialCapital, List<Double> portfolioValues, List<Transaction> transactions, List<Double> benchmarkReturns) {
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
        
        // 计算胜率 - 基于交易记录
        int winCount = 0;
        int totalCount = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("SELL") || transaction.getType().equals("BUY_TO_COVER")) {
                // 这是平仓操作，计算盈亏
                double entryPrice = transaction.getPositionPrice();
                double exitPrice = transaction.getPrice();
                boolean isLongEntry = transaction.isLong();
                
                // 如果是多头平仓，价格上涨则盈利；如果是空头平仓，价格下跌则盈利
                boolean isProfitable = (isLongEntry && exitPrice > entryPrice) || 
                                      (!isLongEntry && exitPrice < entryPrice);
                
                if (isProfitable) {
                    winCount++;
                }
                totalCount++;
            }
        }
        double winRate = totalCount > 0 ? (double)winCount / totalCount : 0;
        
        // 计算与基准策略的比较
        double benchmarkFinalValue = benchmarkReturns.get(benchmarkReturns.size() - 1);
        double benchmarkReturn = (benchmarkFinalValue - initialCapital) / initialCapital;
        
        // 计算其他指标
        Map<String, Object> additionalMetrics = new HashMap<>();
        additionalMetrics.put("totalTrades", transactions.size());
        additionalMetrics.put("profitableTrades", winCount);
        additionalMetrics.put("totalProfit", finalCapital - initialCapital);
        additionalMetrics.put("maxGain", Collections.max(portfolioValues) - initialCapital);
        additionalMetrics.put("maxLoss", Collections.min(portfolioValues) - initialCapital);
        additionalMetrics.put("benchmarkReturn", benchmarkReturn);
        additionalMetrics.put("alpha", totalReturn - benchmarkReturn); // 超额收益
        
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