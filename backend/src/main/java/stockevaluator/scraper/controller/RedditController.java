package stockevaluator.scraper.controller;

import com.fasterxml.jackson.databind.JsonNode;
import stockevaluator.scraper.service.RedditService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import stockevaluator.scraper.model.RedditComment;
import stockevaluator.scraper.model.RedditPost;
import stockevaluator.scraper.model.Ticker;
import stockevaluator.scraper.repository.RedditCommentRepository;
import stockevaluator.scraper.repository.RedditPostRepository;
import stockevaluator.scraper.repository.TickerRepository;


@RestController
@RequestMapping("/api/reddit")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RedditController {

    private final RedditService redditService;
    @Autowired
    private RedditPostRepository postRepository;

    @Autowired
    private RedditCommentRepository commentRepository;

    @Autowired
    private TickerRepository tickerRepository;

    // Get trending tickers (most mentioned)
    @GetMapping("/trending")
    public ResponseEntity<List<Ticker>> getTrendingTickers() {
        List<Ticker> tickers = tickerRepository.findAllByOrderByMentionCountDesc();
        return ResponseEntity.ok(tickers);
    }

    // Get all recent posts
    @GetMapping("/posts")
    public ResponseEntity<List<RedditPost>> getRecentPosts() {
        List<RedditPost> posts = postRepository.findAllByOrderByCreatedDateDesc();
        return ResponseEntity.ok(posts);
    }

    // Get posts mentioning a specific ticker
    @GetMapping("/posts/{ticker}")
    public ResponseEntity<List<RedditPost>> getPostsByTicker(@PathVariable String ticker) {
        List<RedditPost> posts = postRepository.findByTickerMentionsContaining(ticker.toUpperCase());
        return ResponseEntity.ok(posts);
    }

    // Get comments for a specific post
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<RedditComment>> getCommentsByPost(@PathVariable String postId) {
        List<RedditComment> comments = commentRepository.findByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // Get comments mentioning a specific ticker
    @GetMapping("/comments/{ticker}")
    public ResponseEntity<List<RedditComment>> getCommentsByTicker(@PathVariable String ticker) {
        List<RedditComment> comments = commentRepository.findByTickerMentionsContaining(ticker.toUpperCase());
        return ResponseEntity.ok(comments);
    }

    // Get ticker details
    @GetMapping("/ticker/{symbol}")
    public ResponseEntity<Ticker> getTicker(@PathVariable String symbol) {
        return tickerRepository.findById(symbol.toUpperCase())
                .map(ticker -> ResponseEntity.ok().body(ticker))
                .orElse(ResponseEntity.notFound().build());
    }

    // Search for tickers
    @GetMapping("/search")
    public ResponseEntity<List<Ticker>> searchTickers(@RequestParam String query) {
        // For now, return all tickers. You can implement proper search later
        List<Ticker> tickers = tickerRepository.findAllByOrderByMentionCountDesc();
        return ResponseEntity.ok(tickers);
    }
    @GetMapping("/hot")
    public List<JsonNode> getHotPosts() {
        return redditService.getHotPosts();
    }

    @GetMapping("/search/{ticker}")
    public List<JsonNode> searchByTicker(@PathVariable String ticker) {
        return redditService.searchPostsByTicker(ticker);
    }
}
