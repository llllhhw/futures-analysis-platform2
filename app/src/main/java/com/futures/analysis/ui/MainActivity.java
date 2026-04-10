package com.futures.analysis.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.futures.analysis.R;
import com.futures.analysis.ai.AIBasedAnalysisEngine;
import com.futures.analysis.ai.AIAnalysisEngine;
import com.futures.analysis.ai.AnalysisResult;
import com.futures.analysis.ai.TrendPredictionResult;
import com.futures.analysis.backtesting.BacktestResult;
import com.futures.analysis.backtesting.BacktestingEngine;
import com.futures.analysis.data.FuturesData;
import com.futures.analysis.data.NewsData;
import com.futures.analysis.data.impl.MultiExchangeDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Spinner symbolSpinner;
    private Button getDataButton;
    private Button analyzeButton;
    private Button backtestButton;
    private Button selectStartDateButton;
    private Button selectEndDateButton;
    private TextView resultTextView;
    private TextView marketDataTextView;
    private WebView chartWebView;
    private LinearLayout chartContainer;
    
    private MultiExchangeDataSource dataSource;
    private AIAnalysisEngine aiEngine;
    private BacktestingEngine backtestingEngine;
    private List<String> symbolsList;
    private String selectedSymbol;
    
    // 回测时间范围
    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        initializeEngines();
        setupEventListeners();
        loadSymbols();
        
        // 设置默认时间范围为最近6个月
        setDefaultDateRange();
    }

    private void initializeViews() {
        symbolSpinner = findViewById(R.id.symbolSpinner);
        getDataButton = findViewById(R.id.getDataButton);
        analyzeButton = findViewById(R.id.analyzeButton);
        backtestButton = findViewById(R.id.backtestButton);
        selectStartDateButton = findViewById(R.id.selectStartDateButton);
        selectEndDateButton = findViewById(R.id.selectEndDateButton);
        resultTextView = findViewById(R.id.resultTextView);
        marketDataTextView = findViewById(R.id.marketDataTextView);
        chartWebView = findViewById(R.id.chartWebView);
        chartContainer = findViewById(R.id.chartContainer);
        
        // 设置WebView以显示图表
        WebSettings webSettings = chartWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void initializeEngines() {
        dataSource = new MultiExchangeDataSource();
        aiEngine = new AIBasedAnalysisEngine();
        backtestingEngine = new BacktestingEngine();
    }

    private void setupEventListeners() {
        symbolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSymbol = symbolsList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSymbol = null;
            }
        });

        getDataButton.setOnClickListener(v -> getRealTimeData());
        analyzeButton.setOnClickListener(v -> performAIAnalysis());
        backtestButton.setOnClickListener(v -> performBacktesting());
        
        // 添加日期选择按钮监听器
        selectStartDateButton.setOnClickListener(v -> showDatePickerDialog(true));
        selectEndDateButton.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void loadSymbols() {
        symbolsList = dataSource.getSupportedSymbols();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, symbolsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symbolSpinner.setAdapter(adapter);
        
        if (!symbolsList.isEmpty()) {
            selectedSymbol = symbolsList.get(0);
        }
    }
    
    private void setDefaultDateRange() {
        // 设置默认时间为最近6个月
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime();
        cal.add(Calendar.MONTH, -6);
        startDate = cal.getTime();
        
        // 更新按钮显示
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectStartDateButton.setText("开始: " + sdf.format(startDate));
        selectEndDateButton.setText("结束: " + sdf.format(endDate));
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (isStartDate) {
            calendar.setTime(startDate);
        } else {
            calendar.setTime(endDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                Date selectedDate = selectedCalendar.getTime();

                if (isStartDate) {
                    startDate = selectedDate;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectStartDateButton.setText("开始: " + sdf.format(startDate));
                } else {
                    endDate = selectedDate;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectEndDateButton.setText("结束: " + sdf.format(endDate));
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void getRealTimeData() {
        if (selectedSymbol == null) {
            Toast.makeText(this, "请选择期货品种", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FuturesData data = dataSource.getRealTimeData(selectedSymbol);
            displayMarketData(data);
            Toast.makeText(this, "实时数据获取成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "数据获取失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void displayMarketData(FuturesData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("合约代码: ").append(data.getSymbol()).append("\n");
        sb.append("当前价格: ").append(String.format("%.2f", data.getClosePrice())).append("\n");
        sb.append("开盘价: ").append(String.format("%.2f", data.getOpenPrice())).append("\n");
        sb.append("最高价: ").append(String.format("%.2f", data.getHighPrice())).append("\n");
        sb.append("最低价: ").append(String.format("%.2f", data.getLowPrice())).append("\n");
        sb.append("前结算价: ").append(String.format("%.2f", data.getPreSettlement())).append("\n");
        sb.append("涨跌额: ").append(String.format("%.2f", data.getChange())).append("\n");
        sb.append("涨跌幅: ").append(String.format("%.2f%%", data.getChangePercent())).append("\n");
        sb.append("成交量: ").append(String.format("%.0f", data.getVolume())).append("\n");
        sb.append("持仓量: ").append(String.format("%.0f", data.getOpenInterest())).append("\n");
        sb.append("更新时间: ").append(data.getTimestamp().toString());

        marketDataTextView.setText(sb.toString());
    }

    private void performAIAnalysis() {
        if (selectedSymbol == null) {
            Toast.makeText(this, "请选择期货品种", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 获取历史数据（最近30天）
            Date endDate = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            Date startDate = cal.getTime();
            
            List<FuturesData> historicalData = dataSource.getHistoricalData(
                selectedSymbol, 
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(startDate),
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(endDate),
                "daily"
            );
            
            // 获取相关新闻
            List<NewsData> newsData = dataSource.getMarketNews();
            
            // 计算技术指标
            Map<String, Object> technicalIndicators = new HashMap<>();
            if (!historicalData.isEmpty()) {
                technicalIndicators = ((MultiExchangeDataSource) dataSource)
                    .getTechnicalIndicators(selectedSymbol, "daily", 30);
            }
            
            // 执行技术分析
            AnalysisResult techAnalysis = aiEngine.performTechnicalAnalysis(
                selectedSymbol, historicalData, technicalIndicators);
            
            // 执行基本面分析
            AnalysisResult fundAnalysis = aiEngine.performFundamentalAnalysis(
                selectedSymbol, newsData, new HashMap<>());
            
            // 综合预测
            TrendPredictionResult prediction = aiEngine.predictTrend(
                selectedSymbol, techAnalysis, fundAnalysis, historicalData);
            
            // 显示结果
            displayAnalysisResult(prediction);
            
            Toast.makeText(this, "AI分析完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "AI分析失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void displayAnalysisResult(TrendPredictionResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== AI期货分析结果 ===\n\n");
        sb.append("合约代码: ").append(result.getSymbol()).append("\n");
        sb.append("趋势方向: ").append(result.getTrendDirection()).append("\n");
        sb.append("预测概率: ").append(String.format("%.2f%%", result.getProbability() * 100)).append("\n");
        sb.append("目标价格: ").append(String.format("%.2f", result.getTargetPrice())).append("\n");
        sb.append("预测周期: ").append(result.getTimeFrame()).append("\n");
        sb.append("风险等级: ").append(result.getRiskLevel()).append("\n");
        sb.append("入场点: ").append(result.getEntryPoint()).append("\n");
        sb.append("出场点: ").append(result.getExitPoint()).append("\n\n");
        sb.append("分析理由: \n").append(result.getAnalysisReason()).append("\n\n");
        sb.append("关键因素: \n");
        for (String factor : result.getKeyFactors()) {
            sb.append("- ").append(factor).append("\n");
        }

        resultTextView.setText(sb.toString());
    }

    private void performBacktesting() {
        if (selectedSymbol == null) {
            Toast.makeText(this, "请选择期货品种", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "请选择回测时间范围", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDate.after(endDate)) {
            Toast.makeText(this, "开始日期不能晚于结束日期", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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
                    // 返回所有新闻，因为新闻没有精确的时间过滤
                    return dataSource.getMarketNews();
                }
            };
            
            // 执行回测
            BacktestResult backtestResult = backtestingEngine.runBacktest(
                selectedSymbol, startDate, endDate, initialCapital, dataProvider);
            
            // 显示回测结果
            displayBacktestResult(backtestResult);
            
            // 显示图表
            displayCharts(backtestResult);
            
            Toast.makeText(this, "回测分析完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "回测分析失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void displayBacktestResult(BacktestResult result) {
        if (!result.isSuccess()) {
            resultTextView.setText("回测失败: " + result.getErrorMessage());
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== 回测分析结果 ===\n\n");
        sb.append("合约代码: ").append(result.getSymbol()).append("\n");
        sb.append("初始资金: ").append(String.format("%.2f", result.getInitialCapital())).append("\n");
        sb.append("总收益率: ").append(String.format("%.2f%%", result.getTotalReturn() * 100)).append("\n");
        sb.append("夏普比率: ").append(String.format("%.4f", result.getSharpeRatio())).append("\n");
        sb.append("最大回撤: ").append(String.format("%.2f%%", result.getMaxDrawdown() * 100)).append("\n");
        sb.append("胜率: ").append(String.format("%.2f%%", result.getWinRate() * 100)).append("\n");
        
        if (result.getAdditionalMetrics() != null) {
            sb.append("\n额外指标:\n");
            for (Map.Entry<String, Object> entry : result.getAdditionalMetrics().entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        sb.append("\n交易记录:\n");
        int count = 0;
        for (com.futures.analysis.backtesting.Transaction transaction : result.getTransactions()) {
            if (count++ >= 5) { // 只显示前5条交易记录
                sb.append("... 更多交易记录\n");
                break;
            }
            sb.append("- ").append(transaction.getType())
              .append(" ").append(String.format("%.2f", transaction.getQuantity()))
              .append(" @ ").append(String.format("%.2f", transaction.getPrice()))
              .append(" (").append(transaction.getTimestamp()).append(")\n");
        }

        resultTextView.setText(sb.toString());
    }
    
    private void displayCharts(BacktestResult result) {
        if (result.getDates() != null && result.getDates().size() > 0) {
            // 生成图表HTML
            String chartHtml = generateChartHtml(result);
            chartWebView.loadDataWithBaseURL(null, chartHtml, "text/html", "UTF-8", null);
            chartContainer.setVisibility(View.VISIBLE);
        } else {
            chartContainer.setVisibility(View.GONE);
        }
    }
    
    private void displayCharts(BacktestResult result) {
        if (result.getDates() != null && result.getDates().size() > 0) {
            // 从assets加载图表模板
            try {
                String htmlContent = loadChartTemplate();
                
                // 将回测数据注入到HTML中
                chartWebView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
                
                // 使用JavaScript将数据传递给图表
                chartWebView.addJavascriptInterface(new ChartDataBridge(result), "ChartDataBridge");
                
                // 延迟执行JavaScript以确保页面加载完成
                chartWebView.post(() -> {
                    chartWebView.loadUrl("javascript:receiveChartData(" +
                        formatListForJs(result.getDates()) + ", " +
                        formatListForJs(result.getPortfolioValues()) + ", " +
                        formatListForJs(result.getActualPrices()) + ", " +
                        formatListForJs(result.getBenchmarkReturns()) + ", " +
                        formatListForJs(result.getPredictedPrices()) + ")");
                });
                
                chartContainer.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                chartContainer.setVisibility(View.GONE);
            }
        } else {
            chartContainer.setVisibility(View.GONE);
        }
    }
    
    private String loadChartTemplate() {
        try {
            java.io.InputStream inputStream = getAssets().open("chart_template.html");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            // 返回一个简单的备用HTML
            return "<html><body><p>无法加载图表</p></body></html>";
        }
    }
    
    private String formatListForJs(List<?> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            if (list.get(i) instanceof String) {
                sb.append("'").append(list.get(i)).append("'");
            } else if (list.get(i) instanceof Date) {
                sb.append("'").append(new SimpleDateFormat("MM-dd", Locale.getDefault()).format(list.get(i))).append("'");
            } else {
                sb.append(list.get(i));
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    // JavaScript接口类
    public class ChartDataBridge {
        private BacktestResult result;
        
        public ChartDataBridge(BacktestResult result) {
            this.result = result;
        }
        
        @android.webkit.JavascriptInterface
        public String getDates() {
            return formatListForJs(result.getDates());
        }
        
        @android.webkit.JavascriptInterface
        public String getPortfolioValues() {
            return formatListForJs(result.getPortfolioValues());
        }
        
        @android.webkit.JavascriptInterface
        public String getActualPrices() {
            return formatListForJs(result.getActualPrices());
        }
        
        @android.webkit.JavascriptInterface
        public String getBenchmarkReturns() {
            return formatListForJs(result.getBenchmarkReturns());
        }
        
        @android.webkit.JavascriptInterface
        public String getPredictedPrices() {
            return formatListForJs(result.getPredictedPrices());
        }
    }
}