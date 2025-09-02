package com.dress.dressrenting.provider;

import com.dress.dressrenting.exception.exceptions.InvalidInputException;
import com.dress.dressrenting.exception.exceptions.UnMatchedPasswordException;
import com.dress.dressrenting.model.CustomUserDetails;
import com.dress.dressrenting.model.User;
import com.dress.dressrenting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found with username: {}", email);
            return new InvalidInputException("User not found with username: " + email);
        });

        if (!user.getActive()) {
            throw new DisabledException("User is not active");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnMatchedPasswordException("Invalid email or password");
        }
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
