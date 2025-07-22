package WishPool.Be.util.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    @Value("${spring.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.jwt.access-token-expiration}")
    private Long accessTokenExp;

    public GeneratedToken generateToken(Long userId, String role, String name) {
        String accessToken = generateAccessToken(userId, role, name);
        log.info("액세스 토큰 {}", accessToken);
        return new GeneratedToken(accessToken);
    }

    private String generateAccessToken(Long userId, String role, String name) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        java.util.Date now = new Date();
        claims.put("role", role);
        claims.put("name", name);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExp))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean verifyAccessToken(String token){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build().parseClaimsJws(token);
            if(claims.getBody().get("role",String.class).isEmpty()){
                log.warn("해당 토큰은 리프레시 토큰입니다.");
                throw new RuntimeException("액세스 토큰이 아닙니다.");
            }
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e){
            return false;
        }
    }

    public String getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    public String getName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("name", String.class);
    }

    // 토큰에서 ROLE(권한)만 추출한다.
    public String getRole(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role", String.class);
    }
}