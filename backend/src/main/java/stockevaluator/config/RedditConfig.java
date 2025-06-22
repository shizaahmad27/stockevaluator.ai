package stockevaluator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import jakarta.annotation.PostConstruct; // Updated import for Spring Boot 3

import java.util.Base64;

@Configuration
@EnableScheduling
public class RedditConfig {

    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    @Value("${reddit.username}")
    private String username;

    @Value("${reddit.password}")
    private String password;

    private String accessToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            refreshAccessToken();
        } catch (Exception e) {
            System.err.println("Warning: Could not get Reddit access token on startup: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        if (accessToken == null) {
            refreshAccessToken(); // Fallback if somehow null
        }
        return accessToken;
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshAccessToken() {
        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            System.err.println("Warning: Reddit API credentials are not configured. Skipping token refresh.");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://www.reddit.com/api/v1/access_token");

            String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            request.setHeader("Authorization", "Basic " + credentials);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setHeader("User-Agent", "StockEvaluator/1.0.0");

            StringEntity entity = new StringEntity("grant_type=password&username=" + username + "&password=" + password);
            request.setEntity(entity);

            accessToken = client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                System.out.println("Reddit API Response: " + json);
                JsonNode root = objectMapper.readTree(json);

                if (root.has("error")) {
                    throw new RuntimeException("Reddit API Error: " + root.get("error").asText() +
                            " - " + root.path("error_description").asText());
                }

                JsonNode accessTokenNode = root.get("access_token");
                if (accessTokenNode == null) {
                    throw new RuntimeException("No access_token in response: " + json);
                }

                return accessTokenNode.asText();
            });

            System.out.println("Successfully obtained Reddit access token");
        } catch (Exception e) {
            System.err.println("Failed to refresh Reddit access token: " + e.getMessage());
            throw new RuntimeException("Error refreshing Reddit access token", e);
        }
    }
}
