package com.example.challenge.config;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.challenge.config.dto.LoginRequest;
import com.example.challenge.config.dto.LoginResponse;
import com.example.challenge.users.UsersRepository;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;

    private final UsersRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UsersRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
       var user = userRepository.findByUsername(loginRequest.username());

       if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid username or password");
       }

       var now = Instant.now();
       var expiresIn = 300L;

       var claims = JwtClaimsSet.builder()
       .issuer("mybackend")
       .subject(user.get().getUserId().toString())
       .issuedAt(now)
       .expiresAt(now.plusSeconds(expiresIn))
       .build();

       var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

       return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }


}
