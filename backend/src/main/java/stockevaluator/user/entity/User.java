package stockevaluator.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import stockevaluator.user.dto.UserResponse;


/**
 * Entity class representing a user in the system. This class is used to store information about
 * users, including their email, roles, password, and preferences.
 *
 * @since 1.0
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@ToString(exclude = {"verificationTokens"})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  private String firstName;

  private String lastName;

  @Column(nullable = false)
  private boolean notifications = true;

  @Column(nullable = false)
  private boolean emailUpdates = true;

  @Column(nullable = false)
  private boolean locationSharing = true;

  @Column
  private Double latitude;

  @Column
  private Double longitude;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private int passwordRetries = 0;

  @Column
  private LocalDateTime lockedUntil;



  /**
   * Converts the User entity to a UserResponse DTO without location data.
   *
   * @return a UserResponse DTO containing user information without location data.
   */
  public UserResponse toDto() {
    return new UserResponse(
            id,
            email,
            firstName,
            lastName,
            notifications,
            emailUpdates,
            locationSharing,
            null,
            null);
  }

  /**
   * Converts the User entity to a UserResponse DTO including location data. This should only be
   * used in contexts where sharing location is appropriate, such as when getting the active
   * household.
   *
   * @return a UserResponse DTO containing user information including location
   * data.
   */
  public UserResponse toDtoWithLocation() {
    return new UserResponse(
            id,
            email,
            firstName,
            lastName,
            notifications,
            emailUpdates,
            locationSharing,
            latitude,
            longitude);
  }
}
