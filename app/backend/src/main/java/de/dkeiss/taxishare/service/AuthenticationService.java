package de.dkeiss.taxishare.service;

import de.dkeiss.taxishare.config.jwt.JwtUtils;
import de.dkeiss.taxishare.persistence.RoleRepository;
import de.dkeiss.taxishare.persistence.UserRepository;
import de.dkeiss.taxishare.persistence.model.Role;
import de.dkeiss.taxishare.persistence.model.User;
import de.dkeiss.taxishare.service.dto.auth.LoginJwtResponse;
import de.dkeiss.taxishare.service.dto.auth.LoginRequest;
import de.dkeiss.taxishare.service.dto.auth.RegisterRequest;
import de.dkeiss.taxishare.service.dto.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public LoginJwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginJwtResponse(
                jwt,
                "bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    public void register(RegisterRequest registerRequest) {
        Set<String> strRoles = registerRequest.roles();
        if (strRoles == null) {
            strRoles = new HashSet<>();
            strRoles.add("user");
        }
        Set<Role> roles = strRoles.stream()
                .map(role -> roleRepository.findByName(role)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found.")))
                .collect(Collectors.toSet());

        User user = new User(registerRequest.username(),
                registerRequest.email(),
                encoder.encode(registerRequest.password()),
                roles);
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
