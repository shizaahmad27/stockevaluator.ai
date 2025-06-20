package stockevaluator.scraper.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tickers")
public class Ticker {
    @Id
    private String symbol;
    
    private String companyName;
    private Integer mentionCount;
    private Double sentimentScore;
    private Double trendingScore;
    
    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public Integer getMentionCount() { return mentionCount; }
    public void setMentionCount(Integer mentionCount) { this.mentionCount = mentionCount; }
    
    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
    
    public Double getTrendingScore() { return trendingScore; }
    public void setTrendingScore(Double trendingScore) { this.trendingScore = trendingScore; }
} 