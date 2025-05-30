package stockevaluator.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * Custom entry point for handling authentication errors in JWT-based authentication.
 *
 * <p>This class implements the AuthenticationEntryPoint interface and is responsible for
 * returning a custom error response when authentication fails.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED,
        "Unauthorized"
    );
    problemDetail.setType(
            URI.create("https://stockevaluator.ai/errors/401" + HttpStatus.UNAUTHORIZED.value()));
    problemDetail.setTitle(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    response.setContentType("application/problem+json");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
  }
}
