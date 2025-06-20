package stockevaluator.scraper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stockevaluator.scraper.model.RedditPost;

import java.util.List;

@Repository
public interface RedditPostRepository extends JpaRepository<RedditPost, String> {
    List<RedditPost> findByTickerMentionsContaining(String ticker);
    List<RedditPost> findAllByOrderByCreatedDateDesc();
} 