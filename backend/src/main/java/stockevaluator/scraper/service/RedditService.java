package stockevaluator.scraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import stockevaluator.config.RedditConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RedditService {

    private static final String WSB_SUBREDDIT = "wallstreetbets";
    private static final Pattern TICKER_PATTERN = Pattern.compile("\\$([A-Z]{1,5})");
    private static final String REDDIT_API_BASE = "https://oauth.reddit.com";

    private final RedditConfig redditConfig;
    private final ObjectMapper objectMapper;

    public List<JsonNode> getHotPosts() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(REDDIT_API_BASE + "/r/" + WSB_SUBREDDIT + "/hot");
            request.setHeader("Authorization", "Bearer " + redditConfig.getAccessToken());
            request.setHeader("User-Agent", "StockEvaluator/1.0.0");

            return client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                JsonNode root = objectMapper.readTree(json);
                List<JsonNode> posts = new ArrayList<>();
                root.path("data").path("children").forEach(post -> {
                    posts.add(post.path("data"));
                });
                return posts;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error fetching hot posts", e);
        }
    }

    public List<String> extractTickers(String text) {
        List<String> tickers = new ArrayList<>();
        Matcher matcher = TICKER_PATTERN.matcher(text);
        while (matcher.find()) {
            tickers.add(matcher.group(1));
        }
        return tickers;
    }

    public List<JsonNode> searchPostsByTicker(String ticker) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Fixed: Added restrict_sr=true to search only in wallstreetbets
            HttpGet request = new HttpGet(REDDIT_API_BASE + "/r/" + WSB_SUBREDDIT + "/search?q=$" + ticker + "&sort=relevance&restrict_sr=true");
            request.setHeader("Authorization", "Bearer " + redditConfig.getAccessToken());
            request.setHeader("User-Agent", "StockEvaluator/1.0.0");

            return client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                JsonNode root = objectMapper.readTree(json);
                List<JsonNode> posts = new ArrayList<>();
                root.path("data").path("children").forEach(post -> {
                    posts.add(post.path("data"));
                });
                return posts;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error searching posts", e);
        }
    }
}
