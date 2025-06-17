package stockevaluator.scraper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "reddit_posts")
public class RedditPost {
    @Id
    private String id;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String author;
    private Integer score;
    private Integer commentsCount;
    private LocalDateTime createdDate;
    
    @ElementCollection
    @CollectionTable(name = "post_ticker_mentions", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "ticker")
    private Set<String> tickerMentions;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public Set<String> getTickerMentions() { return tickerMentions; }
    public void setTickerMentions(Set<String> tickerMentions) { this.tickerMentions = tickerMentions; }
} 