package stockevaluator.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;
import stockevaluator.auth.dto.*;
import stockevaluator.auth.service.AuthService;
import stockevaluator.config.FrontendConfig;
import stockevaluator.user.dto.UserResponse;


import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing authentication-related operations. Provides endpoints for user
 * registration, login, token refresh, and retrieving user details.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

  private final AuthService authService;
  private final FrontendConfig frontendConfig;

  /**
   * Registers a new user after verifying the CAPTCHA and validating the input.
   *
   * @param request The registration details including Turnstile token.
   * @return ResponseEntity containing the registration response.
   */
  @Operation(summary = "Register a new user", description =
      "Creates a new user account after CAPTCHA "
          + "verification and input validation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered user", content =
      @Content(mediaType = "application/json", schema = @Schema(implementation =
          RegisterResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid registration data or CAPTCHA "
          + "verification failed", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Unexpected server error", content =
      @Content(mediaType = "application/json"))
  })
  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(
      @Parameter(description = "Registration details including Turnstile token", required = true)
      @RequestBody RegisterRequest request) {
    RegisterResponse response = authService.registerAndSendVerificationEmail(request);
    return ResponseEntity.ok(response);
  }




  /**
   * Verifies a user's email address using a token.
   *
   * @param token The verification token.
   * @return ResponseEntity containing the status of the verification operation.
   */
  @PostMapping("/verify-email")
  @Operation(summary = "Verify email address",
      description = "Verifies user's email address using a token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verified successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
  })
  public ResponseEntity<String> verifyEmail(@RequestParam String token) {
    boolean verified = authService.verifyEmail(token);
    if (verified) {
      return ResponseEntity.ok("Email verified successfully.");
    }
    return ResponseEntity.badRequest().body("Invalid or expired verification token.");
  }


  /**
   * Authenticates a user and returns access tokens.
   *
   * @param request The login credentials.
   * @return ResponseEntity containing the access tokens.
   */
  @Operation(summary = "Login user", description = "Authenticates a user and returns access tokens")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated", content =
      @Content(mediaType = "application/json", schema = @Schema(implementation =
          LoginResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "423", description = "Account is locked")
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @Parameter(description = "Login credentials") @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Refreshes the access token using the provided refresh token.
   *
   * @param refreshToken The refresh token used to obtain a new access token.
   * @return ResponseEntity containing the new access token.
   */
  @Operation(summary = "Refresh token", description = "Generates new access token using refresh "
      + "token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully refreshed token", content =
      @Content(mediaType = "application/json", schema = @Schema(implementation =
          RefreshResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid refresh token")
  })
  @PostMapping("/refresh")
  public ResponseEntity<RefreshResponse> refresh(
      @Parameter(description = "Refresh token") @RequestBody RefreshRequest refreshToken) {
    RefreshResponse response = authService.refresh(refreshToken);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves the currently authenticated user's details.
   *
   * @return ResponseEntity containing the user's details.
   */
  @Operation(summary = "Get current user", description = "Retrieves the currently authenticated "
      + "user's details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation =
              UserResponse.class))),
      @ApiResponse(responseCode = "401", description = "Not authenticated")
  })
  @GetMapping("/me")
  public ResponseEntity<UserResponse> me() {
    UserResponse response = authService.me();
    return ResponseEntity.ok(response);
  }

  /**
   * Updates the password of the currently authenticated user.
   *
   * @param updatePasswordRequest The request containing the new password
   * @return ResponseEntity containing the status of the password update operation
   */
//  @Operation(summary = "Update password", description = "Updates the password of the currently "
//      + "authenticated user")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "Password successfully updated", content =
//      @Content(mediaType = "application/json", schema = @Schema(implementation =
//          UpdatePasswordResponse.class))),
//      @ApiResponse(responseCode = "401", description = "Invalid current password or not "
//          + "authenticated")
//  })
//  @PostMapping("/update-password")
//  public ResponseEntity<UpdatePasswordResponse> updatePassword(
//      @RequestBody UpdatePasswordRequest updatePasswordRequest
//  ) {
//    UpdatePasswordResponse response = authService.updatePassword(updatePasswordRequest);
//    return ResponseEntity.ok(response);
//  }

  /**
   * Requests a password reset by generating a reset token and sending it to the user's email.
   *
   * @param request The request containing the user's email
   * @return ResponseEntity containing the status of the reset request operation
   */
//  @Operation(summary = "Request password reset", description = "Generates a password reset token "
//      + "and sends it to the user's email")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "Password reset request successful",
//          content =
//          @Content(mediaType = "application/json", schema = @Schema(implementation =
//              PasswordResetResponse.class))),
//      @ApiResponse(responseCode = "404", description = "User not found"),
//      @ApiResponse(responseCode = "500", description = "Error sending email")
//  })
//  @PostMapping("/request-password-reset")
//  public ResponseEntity<PasswordResetResponse> requestPasswordReset(
//      @RequestBody RequestPasswordResetRequest request
//  ) {
//    PasswordResetResponse response = authService.requestPasswordReset(request);
//    return ResponseEntity.ok(response);
//  }

  /**
   * Completes the password reset process by validating the token and setting a new password.
   *
   * @param request The request containing the email, token, and new password
   * @return ResponseEntity containing the status of the password reset operation
   */
  @Operation(summary = "Complete password reset", description = "Verifies the reset token and "
      + "updates the user's password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password successfully reset", content =
      @Content(mediaType = "application/json", schema = @Schema(implementation =
          PasswordResetResponse.class))),
      @ApiResponse(responseCode = "400", description = "Missing required fields"),
      @ApiResponse(responseCode = "401", description = "Invalid or expired token")
  })
  @PostMapping("/complete-password-reset")
  public ResponseEntity<PasswordResetResponse> completePasswordReset(
      @RequestBody CompletePasswordResetRequest request
  ) {
    PasswordResetResponse response = authService.completePasswordReset(request);
    return ResponseEntity.ok(response);
  }


  /**
   * Exception handler for LockedException to return a 423 (Locked) status code.
   *
   * @param ex The LockedException that was thrown
   * @return ResponseEntity with error details and 423 status code
   */
  @ExceptionHandler(LockedException.class)
  public ResponseEntity<Map<String, String>> handleLockedException(LockedException ex) {
    Map<String, String> response = new HashMap<>();
    response.put("message", ex.getMessage());
    response.put("error", "Account Locked");
    return new ResponseEntity<>(response, HttpStatus.LOCKED); // HTTP 423
  }
}
