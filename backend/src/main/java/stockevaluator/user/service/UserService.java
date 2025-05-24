package stockevaluator.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stockevaluator.user.dto.CreateUser;
import stockevaluator.user.entity.User;
import stockevaluator.user.exception.EmailAlreadyExistsException;
import stockevaluator.user.exception.UserNotFoundException;
import stockevaluator.user.repository.UserRepository;


import java.util.List;
import java.util.UUID;

/**
 * Service class for managing users. This class provides methods to create, update, delete, and
 * retrieve users.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a new user in the system.
   *
   * @param data the user data
   * @return the created User entity
   * @throws EmailAlreadyExistsException if the email is already in use by another user
   */
  public User createUser(CreateUser data) {
    if (userRepository.existsByEmail(data.getEmail())) {
      throw new EmailAlreadyExistsException(
          "User with email " + data.getEmail() + " already exists");
    }

    User user = User.builder().email(data.getEmail())
        .password(passwordEncoder.encode(data.getPassword()))
        .firstName(data.getFirstName()).lastName(data.getLastName())
        .notifications(data.isNotifications()).emailUpdates(data.isEmailUpdates())
        .locationSharing(data.isLocationSharing()).passwordRetries(0)
        .lockedUntil(null).build();
    return userRepository.save(user);
  }

  /**
   * Updates the password of an existing user.
   *
   * @param userId      the UUID of the user to update
   * @param newPassword the new password
   * @return the updated User entity
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  public User updatePassword(UUID userId, String newPassword) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    user.setPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * Updates an existing user's information.
   *
   * @param userId the UUID of the user to update
   * @param data   the updated user data
   * @return the updated User entity
   * @throws UserNotFoundException       if the user with the given ID does not exist
   * @throws EmailAlreadyExistsException if the new email is already in use by another user
   */
  public User updateUser(UUID userId, CreateUser data) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    // Check if email is being changed and if it already exists
    if (!user.getEmail().equals(data.getEmail()) && userRepository.existsByEmail(data.getEmail())) {
      throw new EmailAlreadyExistsException(
          "User with email " + data.getEmail() + " already exists");
    }

    user.setEmail(data.getEmail());
    user.setFirstName(data.getFirstName());
    user.setLastName(data.getLastName());
    user.setNotifications(data.isNotifications());
    user.setEmailUpdates(data.isEmailUpdates());

    // Check if location sharing is being disabled
    if (user.isLocationSharing() && !data.isLocationSharing()) {
      // Clear location data when disabling location sharing
      user.setLatitude(null);
      user.setLongitude(null);
    }

    user.setLocationSharing(data.isLocationSharing());

    // Only update password if it's provided
    if (data.getPassword() != null && !data.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(data.getPassword()));
    }

    return userRepository.save(user);
  }

  /**
   * Deletes a user from the system.
   *
   * @param userId the UUID of the user to delete
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  public void deleteUser(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
    userRepository.deleteById(userId);
  }

  /**
   * Retrieves all users in the system.
   *
   * @return a list of all User entities
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }



  /**
   * Retrieves the currently authenticated user.
   *
   * @return the User entity of the currently authenticated user
   * @throws UserNotFoundException if the user is not found in the database
   */
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
  }



  /**
   * Retrieves a user by their ID.
   *
   * @param id the UUID of the user to retrieve
   * @return the User entity with the specified ID
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  public User getUserById(UUID id) {
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  /**
   * Retrieves a user by their mail.
   *
   * @param email the mail of the user to retrieve
   * @return the User entity with the specified mail
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(
        () -> new UserNotFoundException("User with email " + email + " does not exist"));
  }

  /**
   * Updates a user's location coordinates.
   *
   * @param userId    the UUID of the user to update
   * @param latitude  the latitude coordinate
   * @param longitude the longitude coordinate
   * @return the updated User entity
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  public User updateUserLocation(UUID userId, Double latitude, Double longitude) {
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new UserNotFoundException("User with id " + userId + " does not exist"));

    // Only update location if user has enabled location sharing
    if (user.isLocationSharing()) {
      user.setLatitude(latitude);
      user.setLongitude(longitude);
      return userRepository.save(user);
    }

    // Return the user without updating location if sharing is disabled
    return user;
  }

  /**
   * Checks if the current user is either an admin or the user being accessed.
   *
   * @param userId the UUID of the user being accessed
   * @return true if the current user is an admin or the user being accessed, false otherwise
   */
  public boolean isSelf(UUID userId) {
    User currentUser = getCurrentUser();
    return currentUser.getId().equals(userId);
            }
}
