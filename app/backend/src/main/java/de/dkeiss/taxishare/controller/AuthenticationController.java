package de.dkeiss.taxishare.controller;

import de.dkeiss.taxishare.service.AuthenticationService;
import de.dkeiss.taxishare.service.dto.auth.LoginJwtResponse;
import de.dkeiss.taxishare.service.dto.auth.LoginRequest;
import de.dkeiss.taxishare.service.dto.auth.RegisterRequest;
import de.dkeiss.taxishare.service.dto.auth.SignupMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginJwtResponse loginJwtResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(loginJwtResponse);
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (authenticationService.existsByUsername(registerRequest.username())) {
            return ResponseEntity
                    .badRequest()
                    .body(new SignupMessageResponse("Error: Username is already taken!"));
        }
        if (authenticationService.existsByEmail(registerRequest.email())) {
            return ResponseEntity
                    .badRequest()
                    .body(new SignupMessageResponse("Error: Email is already in use!"));
        }
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(new SignupMessageResponse("User registered successfully!"));
    }
}
