package stockevaluator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StockEvaluatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockEvaluatorApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Stock Evaluator AI - Backend API</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .container { max-width: 800px; margin: 0 auto; }
                    .endpoint { background: #f5f5f5; padding: 10px; margin: 10px 0; border-radius: 5px; }
                    .swagger { background: #007acc; color: white; padding: 10px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🚀 Stock Evaluator AI - Backend API</h1>
                    <p>Your backend is running successfully! Here are some useful endpoints:</p>
                    
                    <h2>📚 API Documentation</h2>
                    <a href="/swagger-ui/" class="swagger">View Swagger UI Documentation</a>
                    
                    <h2>🔐 Authentication Endpoints</h2>
                    <div class="endpoint">
                        <strong>POST /api/auth/register</strong> - Register a new user
                    </div>
                    <div class="endpoint">
                        <strong>POST /api/auth/login</strong> - Login user
                    </div>
                    <div class="endpoint">
                        <strong>POST /api/auth/refresh</strong> - Refresh JWT token
                    </div>
                    
                    <h2>🗄️ Database</h2>
                    <div class="endpoint">
                        <strong>GET /h2-console</strong> - H2 Database Console
                    </div>
                    
                    <h2>📝 Next Steps</h2>
                    <p>To see the full application, you need to deploy the frontend. The frontend is a Vue.js application located in the <code>frontend/</code> directory.</p>
                </div>
            </body>
            </html>
            """;
    }
}
