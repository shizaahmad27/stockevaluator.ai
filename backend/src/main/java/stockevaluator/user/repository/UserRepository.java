package stockevaluator.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stockevaluator.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities. This interface extends JpaRepository to
 * provide CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Finds a User by its email.
   *
   * @param email the email of the User
   * @return an Optional containing the User if found, or empty if not found
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a User exists by its email.
   *
   * @param email the email of the User
   * @return true if the User exists, false otherwise
   */
  boolean existsByEmail(String email);


}
