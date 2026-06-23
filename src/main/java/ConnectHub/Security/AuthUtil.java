package ConnectHub.Security;

import ConnectHub.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
public class AuthUtil {
    String secretKey="ssndlskdr9840hhr@$%#%kshf ojr^^$;hdfaonsrrh9sn";
    private SecretKey generateSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    }
    public String generateJWT(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("username",user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*10))
                .signWith(generateSecretKey())
                .compact();
    }
    public String verifyToken(String token){
        Claims claims=extractClaims(token);
        return claims.getSubject();
    }
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
