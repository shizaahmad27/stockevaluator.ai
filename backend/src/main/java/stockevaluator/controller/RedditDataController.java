package stockevaluator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import stockevaluator.scraper.model.RedditComment;
import stockevaluator.scraper.model.RedditPost;
import stockevaluator.scraper.model.Ticker;
import stockevaluator.scraper.repository.RedditCommentRepository;
import stockevaluator.scraper.repository.RedditPostRepository;
import stockevaluator.scraper.repository.TickerRepository;

import java.util.List;

@RestController
@RequestMapping("/api/reddit")
@CrossOrigin(origins = "${frontend.url}")
public class RedditDataController {

    @Autowired
    private RedditPostRepository postRepository;

    @Autowired
    private RedditCommentRepository commentRepository;

    @Autowired
    private TickerRepository tickerRepository;

    @GetMapping("/posts")
    public List<RedditPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    @GetMapping("/posts/{ticker}")
    public List<RedditPost> getPostsByTicker(@PathVariable String ticker) {
        return postRepository.findByTickerMentionsContaining(ticker);
    }

    @GetMapping("/comments/{postId}")
    public List<RedditComment> getCommentsByPostId(@PathVariable String postId) {
        return commentRepository.findByPostId(postId);
    }

    @GetMapping("/tickers")
    public List<Ticker> getAllTickers() {
        return tickerRepository.findAllByOrderByMentionCountDesc();
    }

    @GetMapping("/tickers/trending")
    public List<Ticker> getTrendingTickers() {
        return tickerRepository.findAllByOrderByTrendingScoreDesc();
    }
} 