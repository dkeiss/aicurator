package de.dkeiss.taxishare.config.jwt;

import de.dkeiss.taxishare.service.dto.auth.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .signWith(priv)
                .compact();
    }

}
