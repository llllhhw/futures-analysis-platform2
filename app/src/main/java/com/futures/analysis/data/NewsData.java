package com.futures.analysis.data;

import java.util.Date;

/**
 * 新闻数据实体类
 */
public class NewsData {
    private String title;         // 新闻标题
    private String content;       // 新闻内容
    private String source;        // 新闻来源
    private Date publishTime;     // 发布时间
    private String category;      // 新闻分类（如：宏观、行业、政策等）
    private int importance;       // 重要性等级（1-5级）
    private String relatedSymbols; // 相关期货品种
    
    // 构造函数
    public NewsData() {}
    
    public NewsData(String title, String content, String source, Date publishTime, 
                   String category, int importance, String relatedSymbols) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.publishTime = publishTime;
        this.category = category;
        this.importance = importance;
        this.relatedSymbols = relatedSymbols;
    }
    
    // Getter和Setter方法
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getImportance() { return importance; }
    public void setImportance(int importance) { this.importance = importance; }
    
    public String getRelatedSymbols() { return relatedSymbols; }
    public void setRelatedSymbols(String relatedSymbols) { this.relatedSymbols = relatedSymbols; }
}