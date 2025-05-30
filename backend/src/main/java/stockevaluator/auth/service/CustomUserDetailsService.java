package stockevaluator.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stockevaluator.user.entity.User;
import stockevaluator.user.repository.UserRepository;

/**
 * Custom implementation of UserDetailsService for loading user-specific data during
 * authentication.
 *
 * <p>This class is responsible for retrieving user details from the database and converting them
 * into a format suitable for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

    return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
        .password(user.getPassword()).build();
  }
}
