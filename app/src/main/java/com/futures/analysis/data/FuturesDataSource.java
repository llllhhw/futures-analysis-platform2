package com.futures.analysis.data;

import java.util.List;
import java.util.Map;

/**
 * 期货数据源接口
 */
public interface FuturesDataSource {
    /**
     * 获取指定期货合约的实时行情
     * @param symbol 期货合约代码
     * @return 行情数据
     */
    FuturesData getRealTimeData(String symbol);

    /**
     * 批量获取多个期货合约的实时行情
     * @param symbols 期货合约代码列表
     * @return 行情数据列表
     */
    List<FuturesData> getBatchRealTimeData(List<String> symbols);

    /**
     * 获取历史行情数据
     * @param symbol 期货合约代码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param period 数据周期（分钟、小时、日等）
     * @return 历史行情数据
     */
    List<FuturesData> getHistoricalData(String symbol, String startTime, String endTime, String period);

    /**
     * 获取支持的期货品种列表
     * @return 期货品种列表
     */
    List<String> getSupportedSymbols();

    /**
     * 获取市场新闻和公告
     * @return 新闻公告列表
     */
    List<NewsData> getMarketNews();
}