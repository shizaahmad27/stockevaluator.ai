package stockevaluator.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stockevaluator.user.entity.User;

import java.time.Instant;

/**
 * Entity class representing a password reset token. This token is used for resetting user passwords
 * and contains the token string, associated user, and expiry date.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String token;

  @OneToOne
  private User user;

  @Column(nullable = false)
  private Instant expiryDate;

  public boolean isExpired() {
    return Instant.now().isAfter(expiryDate);
  }
}
