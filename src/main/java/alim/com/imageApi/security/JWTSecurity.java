package alim.com.imageApi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTSecurity {
    private final String password;
    private final String subject;
    private final String issuer;

    public JWTSecurity(@Value("${jwt.password}") String password,
                       @Value("${jwt.subject}") String subject,
                       @Value("${jwt.issuer}") String issuer) {
        this.password = password;
        this.subject = subject;
        this.issuer = issuer;
    }

    public String generateToken(String userEmail) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());

        return JWT.create()
                .withSubject(subject)
                .withClaim("userEmail",userEmail)
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(password));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(password))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("userEmail").asString();
    }
}
