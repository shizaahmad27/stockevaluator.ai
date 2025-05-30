package stockevaluator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for representing a registration response. This class is used to
 * transfer data between the server and client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

  @NotBlank
  private String message;
  @NotBlank
  private boolean success;
}
