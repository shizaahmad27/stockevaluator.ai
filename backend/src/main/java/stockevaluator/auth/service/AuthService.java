package stockevaluator.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stockevaluator.auth.config.JwtProperties;
import stockevaluator.auth.dto.*;
import stockevaluator.auth.entity.PasswordResetToken;
import stockevaluator.auth.entity.RefreshToken;
import stockevaluator.auth.exception.RefreshTokenDoesNotExistException;
import stockevaluator.auth.exception.InvalidCredentialsException;
import stockevaluator.auth.exception.InvalidTokenException;
import stockevaluator.auth.repository.PasswordResetTokenRepository;
import stockevaluator.auth.repository.RefreshTokenRepository;
import stockevaluator.config.FrontendConfig;
import stockevaluator.email.entity.VerificationToken;
import stockevaluator.email.service.EmailVerificationService;
import stockevaluator.user.dto.CreateUser;
import stockevaluator.user.dto.UserResponse;
import stockevaluator.user.entity.User;
import stockevaluator.user.exception.UserNotFoundException;
import stockevaluator.user.repository.UserRepository;
import stockevaluator.user.service.UserService;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for handling authentication-related operations such as user registration, login,
 * token generation, and token refresh.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final CustomUserDetailsService userDetailsService;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;
  private final EmailVerificationService emailVerificationService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final JwtProperties jwtProperties;
  private final FrontendConfig frontendConfig;

  /**
   * Registers a new user and sends verification email.
   *
   * @param registerRequest The registration request containing user details.
   * @return A response containing the access and refresh tokens.
   */
  @Transactional
  public RegisterResponse registerAndSendVerificationEmail(RegisterRequest registerRequest) {
    User user = userService.createUser(new CreateUser(
        registerRequest.getEmail(),
        registerRequest.getPassword(),
        registerRequest.getFirstName(),
        registerRequest.getLastName(),
        true,
        true,
        true
    ));

//    // Send verification email, should throw if error occurs, which will be caught by the
//    // transaction
//    VerificationToken token = emailVerificationService.createVerificationToken(user);
//    emailVerificationService.sendVerificationEmail(user, token);

    return new RegisterResponse("User registered successfully. Verification email sent.", true);
  }



  /**
   * Authenticates a user and generates access and refresh tokens.
   *
   * @param loginRequest The login request containing user credentials.
   * @return A response containing the access and refresh tokens.
   */
  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userService.getUserByEmail(loginRequest.getEmail());
    if (user == null) {
      log.warn("Login attempt for non-existing user: {}", loginRequest.getEmail());
      throw new UserNotFoundException("User not found");
    }
//    if (!user.isEmailVerified()) {
//      throw new EmailNotVerifiedException(
//          "Email address not verified. Please verify your email before logging in.");
//    }

    // Check if account is locked
    if (user.getLockedUntil() != null && LocalDateTime.now()
        .isBefore(user.getLockedUntil())) {
      log.warn("Account locked attempt for user: {}. Locked until: {}", user.getEmail(),
          user.getLockedUntil());
      throw new LockedException("Account is locked until " + user.getLockedUntil());
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getPassword()));

      // Reset password retries on successful login
      if (user.getPasswordRetries() > 0) {
        user.setPasswordRetries(0);
        user.setLockedUntil(LocalDateTime.now().minusMinutes(1));
        userRepository.save(user);
      }
    } catch (Exception e) {
      // Handle failed login attempts
      user.setPasswordRetries(user.getPasswordRetries() + 1);

      // Lock account after 5 failed attempts
      if (user.getPasswordRetries() >= 5) {
        user.setLockedUntil(LocalDateTime.now().plusMinutes(5));
        log.info("Account locked for user: {} until {}", user.getEmail(), user.getLockedUntil());
      }

      userRepository.save(user);
      throw e;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

    String accessToken = tokenService.generateAccessToken(userDetails);
    String refreshToken = tokenService.generateRefreshToken(userDetails);

    refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).user(user).build());

    return new LoginResponse(
        accessToken,
        refreshToken);
  }

  /**
   * Refreshes the access token using the provided refresh token.
   *
   * @param refreshRequest The request containing the refresh token.
   * @return A response containing the new access and refresh tokens.
   */
  @Transactional
  public RefreshResponse refresh(RefreshRequest refreshRequest) {
    if (!tokenService.isRefreshToken(refreshRequest.getRefreshToken())) {
      throw new InvalidTokenException();
    }

    String email = tokenService.extractEmail(refreshRequest.getRefreshToken());
    if (email == null) {
      throw new InvalidTokenException();
    }

    log.info("Refreshing token");

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    if (!tokenService.isValid(refreshRequest.getRefreshToken(), userDetails)) {
      throw new InvalidTokenException();
    }

    RefreshToken existingToken = refreshTokenRepository.findByToken(
        refreshRequest.getRefreshToken()).orElseThrow(
        RefreshTokenDoesNotExistException::new);

    User user = userService.getUserByEmail(email);

    String accessToken = tokenService.generateAccessToken(userDetails);
    String refreshToken = tokenService.generateRefreshToken(userDetails);

    refreshTokenRepository.delete(existingToken);
    refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).user(user).build());

    return new RefreshResponse(
        accessToken,
        refreshToken);
  }

  /**
   * Verifies the email address using the provided token.
   *
   * @param token The verification token.
   * @return true if the email is verified, false otherwise.
   */
  @Transactional
  public boolean verifyEmail(String token) {
    boolean verified = emailVerificationService.verifyToken(token);
    if (verified) {
      // Any additional action after verification
      log.info("Email verified successfully with token: {}", token);
    } else {
      log.warn("Failed verification attempt with token: {}", token);
    }
    return verified;
  }

  /**
   * Retrieves the currently authenticated user's details.
   *
   * @return UserResponse containing the user's details.
   */
  public UserResponse me() {
    return userService.getCurrentUser().toDto();
  }

//  /**
//   * Updates the password of the currently authenticated user.
//   *
//   * @param updatePasswordRequest The request containing the new password.
//   * @return UpdatePasswordResponse containing the status of the update.
//   */
//  @Transactional
//  public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
//    User user = userService.getCurrentUser();
//    log.info("Starting password update process for user: {}", user.getEmail());
//
//    // Verify current password
//    if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
//      log.warn("Password update failed - incorrect current password for user: {}", user.getEmail());
//      throw new InvalidCredentialsException("Current password is incorrect");
//    }
//
//    // Update password
//    log.info("Updating password for user: {}", user.getEmail());
//    userService.updatePassword(user.getId(), updatePasswordRequest.getPassword());
//
//    // Generate reset token for security
//    log.info("Generating reset token for user: {}", user.getEmail());
//    String token = tokenService
//        .generateResetPasswordToken(user.getEmail());
//    long expirationHours = jwtProperties.getResetPasswordTokenExpiration() / (1000 * 60 * 60);
//
//    // Save token
//    PasswordResetToken passwordResetToken = PasswordResetToken.builder()
//        .token(token)
//        .user(user)
//        .expiryDate(Instant.now().plus(Duration.ofHours(expirationHours)))
//        .build();
//
//    passwordResetTokenRepository.save(passwordResetToken);
//    log.info("Reset token saved for user: {}", user.getEmail());
//
//    // Send notification email with reset link
//    String resetLink = frontendConfig.getUrl() + "/verifiser-passord-tilbakestilling?token="
//        + java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
//    log.info("Sending password change notification email to: {}", user.getEmail());
//    ResponseEntity<String> emailResponse = emailVerificationService.sendPasswordChangeNotification(
//        user, resetLink, expirationHours);
//
//    if (emailResponse.getStatusCode().is2xxSuccessful()) {
//      log.info("Password change notification email sent successfully to: {}", user.getEmail());
//    } else {
//      log.error(
//          "Failed to send password change notification email to: {}. Status: {}, Response: {}",
//          user.getEmail(), emailResponse.getStatusCode(), emailResponse.getBody());
//    }
//
//    return new UpdatePasswordResponse("Password updated successfully", true);
//  }
//
//  /**
//   * Requests a password reset by generating a reset token and sending it to the user's email.
//   *
//   * @param request The request containing the user's email.
//   * @return A response indicating the success of the operation.
//   */
//  @Transactional
//  public PasswordResetResponse requestPasswordReset(
//      RequestPasswordResetRequest request) {
//    User user = userService.getUserByEmail(request.getEmail());
//
//    List<PasswordResetToken> existingTokens = passwordResetTokenRepository.findByUser(user);
//
//    if (!existingTokens.isEmpty()) {
//      passwordResetTokenRepository.deleteAll(existingTokens);
//    }
//
//    String token = tokenService.generateResetPasswordToken(user.getEmail());
//
//    PasswordResetToken passwordResetToken = PasswordResetToken.builder().token(token).user(user)
//        .expiryDate(
//            Instant.now().plusMillis(jwtProperties.getResetPasswordTokenExpiration()))
//        .build();
//
//    passwordResetTokenRepository.save(passwordResetToken);
//
//    // Send password reset email
//    String resetLink =
//        frontendConfig.getUrl() + "/verifiser-passord-tilbakestilling?token="
//            + java.net.URLEncoder.encode(
//            token, java.nio.charset.StandardCharsets.UTF_8);
//    long expirationHours = jwtProperties.getResetPasswordTokenExpiration() / (1000 * 60 * 60);
//
//    emailVerificationService.sendPasswordResetEmail(
//        user,
//        resetLink,
//        expirationHours
//    );
//
//    return PasswordResetResponse.builder()
//        .message("Reset password request sent to " + user.getEmail())
//        .success(true)
//        .build();
//  }

  /**
   * Completes the password reset process by validating the token and updating the user's password.
   *
   * @param request The request containing the token and new password.
   * @return A response indicating the success of the operation.
   */
  @Transactional
  public PasswordResetResponse completePasswordReset(CompletePasswordResetRequest request) {
    if (request.getToken() == null) {
      throw new InvalidCredentialsException("Token is required");
    }

    if (request.getNewPassword() == null) {
      throw new InvalidCredentialsException("New password is required");
    }

    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(
            request.getToken())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid token"));

    if (passwordResetToken.isExpired()) {
      passwordResetTokenRepository.delete(passwordResetToken);
      throw new InvalidCredentialsException("Expired token");
    }

    User user = passwordResetToken.getUser();
    userService.updatePassword(user.getId(), request.getNewPassword());
    passwordResetTokenRepository.delete(passwordResetToken);

    return PasswordResetResponse.builder()
        .message("Password updated")
        .success(true)
        .build();
  }



  }

