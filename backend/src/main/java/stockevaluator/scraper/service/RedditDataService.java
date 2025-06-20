package stockevaluator.scraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stockevaluator.config.RedditConfig;
import stockevaluator.scraper.model.RedditComment;
import stockevaluator.scraper.model.RedditPost;
import stockevaluator.scraper.model.Ticker;
import stockevaluator.scraper.repository.RedditCommentRepository;
import stockevaluator.scraper.repository.RedditPostRepository;
import stockevaluator.scraper.repository.TickerRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RedditDataService {

    @Autowired
    private RedditConfig redditConfig;

    @Autowired
    private RedditPostRepository postRepository;

    @Autowired
    private RedditCommentRepository commentRepository;

    @Autowired
    private TickerRepository tickerRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern TICKER_PATTERN = Pattern.compile("\\$([A-Z]{1,5})");

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void fetchAndStoreRedditData() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Fetch posts from r/wallstreetbets
            HttpGet request = new HttpGet("https://oauth.reddit.com/r/wallstreetbets/hot");
            request.setHeader("Authorization", "Bearer " + redditConfig.getAccessToken());
            request.setHeader("User-Agent", "StockEvaluator/1.0.0");

            client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                JsonNode root = objectMapper.readTree(json);
                JsonNode posts = root.path("data").path("children");

                for (JsonNode post : posts) {
                    JsonNode data = post.path("data");
                    RedditPost redditPost = new RedditPost();
                    redditPost.setId(data.path("id").asText());
                    redditPost.setTitle(data.path("title").asText());
                    redditPost.setContent(data.path("selftext").asText());
                    redditPost.setAuthor(data.path("author").asText());
                    redditPost.setScore(data.path("score").asInt());
                    redditPost.setCommentsCount(data.path("num_comments").asInt());
                    redditPost.setCreatedDate(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(data.path("created_utc").asLong()),
                        ZoneId.systemDefault()
                    ));

                    // Extract ticker mentions
                    Set<String> tickerMentions = extractTickerMentions(
                        data.path("title").asText() + " " + data.path("selftext").asText()
                    );
                    redditPost.setTickerMentions(tickerMentions);

                    // Save post
                    postRepository.save(redditPost);

                    // Update ticker statistics
                    updateTickerStatistics(tickerMentions);

                    // Fetch comments
                    fetchComments(redditPost.getId());
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchComments(String postId) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://oauth.reddit.com/comments/" + postId);
            request.setHeader("Authorization", "Bearer " + redditConfig.getAccessToken());
            request.setHeader("User-Agent", "StockEvaluator/1.0.0");

            client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                JsonNode root = objectMapper.readTree(json);
                JsonNode comments = root.path(1).path("data").path("children");

                for (JsonNode comment : comments) {
                    JsonNode data = comment.path("data");
                    if (data.has("body")) {
                        RedditComment redditComment = new RedditComment();
                        redditComment.setId(data.path("id").asText());
                        redditComment.setContent(data.path("body").asText());
                        redditComment.setAuthor(data.path("author").asText());
                        redditComment.setScore(data.path("score").asInt());
                        redditComment.setCreatedDate(LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(data.path("created_utc").asLong()),
                            ZoneId.systemDefault()
                        ));

                        // Extract ticker mentions
                        Set<String> tickerMentions = extractTickerMentions(data.path("body").asText());
                        redditComment.setTickerMentions(tickerMentions);

                        // Save comment
                        commentRepository.save(redditComment);

                        // Update ticker statistics
                        updateTickerStatistics(tickerMentions);
                    }
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> extractTickerMentions(String text) {
        Set<String> tickers = new HashSet<>();
        Matcher matcher = TICKER_PATTERN.matcher(text);
        while (matcher.find()) {
            tickers.add(matcher.group(1));
        }
        return tickers;
    }

    private void updateTickerStatistics(Set<String> tickerMentions) {
        for (String ticker : tickerMentions) {
            Ticker tickerEntity = tickerRepository.findById(ticker)
                .orElse(new Ticker());
            
            if (tickerEntity.getSymbol() == null) {
                tickerEntity.setSymbol(ticker);
                tickerEntity.setMentionCount(1);
                tickerEntity.setSentimentScore(0.0);
                tickerEntity.setTrendingScore(0.0);
            } else {
                tickerEntity.setMentionCount(tickerEntity.getMentionCount() + 1);
            }
            
            tickerRepository.save(tickerEntity);
        }
    }
} 