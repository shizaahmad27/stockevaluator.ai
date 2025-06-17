package stockevaluator.scraper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stockevaluator.scraper.model.RedditComment;

import java.util.List;

@Repository
public interface RedditCommentRepository extends JpaRepository<RedditComment, String> {
    List<RedditComment> findByPostId(String postId);
    List<RedditComment> findByTickerMentionsContaining(String ticker);
} 