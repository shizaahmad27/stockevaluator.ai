package stockevaluator.scraper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stockevaluator.scraper.model.Ticker;

import java.util.List;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, String> {
    List<Ticker> findAllByOrderByMentionCountDesc();
    List<Ticker> findAllByOrderByTrendingScoreDesc();
} 