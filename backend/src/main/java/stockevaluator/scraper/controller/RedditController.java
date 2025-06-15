package stockevaluator.scraper.controller;

import com.fasterxml.jackson.databind.JsonNode;
import stockevaluator.scraper.service.RedditService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/reddit")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RedditController {

    private final RedditService redditService;

    @GetMapping("/hot")
    public List<JsonNode> getHotPosts() {
        return redditService.getHotPosts();
    }

    @GetMapping("/search/{ticker}")
    public List<JsonNode> searchByTicker(@PathVariable String ticker) {
        return redditService.searchPostsByTicker(ticker);
    }
}
