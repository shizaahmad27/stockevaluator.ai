package stockevaluator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class StockEvaluatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockEvaluatorApplication.class, args);
    }
}
