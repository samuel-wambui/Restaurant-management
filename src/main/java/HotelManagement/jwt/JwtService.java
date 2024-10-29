package HotelManagement.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Autowired
    JwtBlacklistService jwtBlacklistService;
    private final String secretKey;

    private JwtService() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Generate token with authorities
    public String generateToken(String username, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities); // Add authorities to claims

        return Jwts.builder()
                .setClaims(claims) // Set claims
                .setSubject(username) // Set subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Issued time
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 min expiration
                .signWith(getKey()) // Sign with secret key
                .compact(); // Build token
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(String username, List<String> authorities) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days expiration
                .signWith(getKey())
                .compact();
    }

    // Validate refresh token
    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract authorities (roles/permissions) from token
    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> authorities = claims.get("authorities", List.class);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);

        boolean isValidUsername = username.equals(userDetails.getUsername());
        boolean isExpired = isTokenExpired(token);
        boolean isBlacklisted = jwtBlacklistService.isTokenBlacklisted(token);

        return isValidUsername && !isExpired && !isBlacklisted;
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
