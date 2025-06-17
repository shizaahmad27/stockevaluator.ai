package stockevaluator.scraper.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "reddit_comments")
public class RedditComment {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private RedditPost post;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String author;
    private Integer score;
    private LocalDateTime createdDate;
    
    @ElementCollection
    @CollectionTable(name = "comment_ticker_mentions", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "ticker")
    private Set<String> tickerMentions;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public RedditPost getPost() { return post; }
    public void setPost(RedditPost post) { this.post = post; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public Set<String> getTickerMentions() { return tickerMentions; }
    public void setTickerMentions(Set<String> tickerMentions) { this.tickerMentions = tickerMentions; }
} 