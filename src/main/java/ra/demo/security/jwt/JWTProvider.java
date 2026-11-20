package ra.demo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Component
public class JWTProvider {
    @Value("${application.jwt.secret}")
    private String secret;

    public Claims extractClaimsJws(String jwt) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    }

    //Trích xuất 1 claim cụ thể
    public <T> T extractClaimsJws(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaimsJws(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromJws(String jwt) {
        Claims claims = extractClaimsJws(jwt);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            System.err.println("JWT Validation Error: " + e.getMessage());
            return false; // Token không hợp lệ
        }
    }
}
