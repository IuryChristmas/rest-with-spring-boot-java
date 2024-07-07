package br.com.erudio.security.jwt;

import br.com.erudio.exceptions.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        String scopes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validityInMilliseconds))
                .subject(username)
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        String scopes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validityInMilliseconds * 3))
                .subject(username)
                .claim("scope", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean validateToken(String token) {
        var decodedToken = JWT.decode(token);
        try {
            return !decodedToken.getExpiresAt().before(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
        }
    }

}
