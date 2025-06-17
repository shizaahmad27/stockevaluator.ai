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
        refreshAccessToken(); // Get token immediately on startup
    }

    public String getAccessToken() {
        if (accessToken == null) {
            refreshAccessToken(); // Fallback if somehow null
        }
        return accessToken;
    }

    @Scheduled(fixedRate = 3600000) // Refresh token every hour
    public void refreshAccessToken() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://www.reddit.com/api/v1/access_token");

            String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            request.setHeader("Authorization", "Basic " + credentials);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity("grant_type=password&username=" + username + "&password=" + password);
            request.setEntity(entity);

            accessToken = client.execute(request, response -> {
                String json = EntityUtils.toString(response.getEntity());
                JsonNode root = objectMapper.readTree(json);
                return root.get("access_token").asText();
            });
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing Reddit access token", e);
        }
    }
}
