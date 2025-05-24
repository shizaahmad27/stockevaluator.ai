package stockevaluator.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stockevaluator.decorators.ValidEmail;
import stockevaluator.decorators.ValidPassword;


/**
 * Data Transfer Object (DTO) for representing a login request. This class is used to transfer data
 * between the server and client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @ValidEmail
  private String email;
  @ValidPassword
  private String password;
}
