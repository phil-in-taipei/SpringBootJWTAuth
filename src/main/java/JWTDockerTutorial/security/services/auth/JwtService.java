package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.logging.BatchLogger;
import JWTDockerTutorial.security.logging.Loggable;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// the methods are logged using the BatchLogger, which only
// shows the number of args; not the String values of the arguments
@Service
public class JwtService {

    // TODO for all app applications: generate new one and store as environment variable
    private static final String SECRET_KEY = "050b872ced72e5c140063eefc0cb17b7050b872ced72e5c140063eefc0cb17b7";

    @BatchLogger
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @BatchLogger
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        System.out.println("These are the claims: " + claims);
        return claimsResolver.apply(claims);
    }

    @BatchLogger
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // TODO: 2/14/2024 double check expiration time
    @BatchLogger
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // this expiration is just over 10 minutes
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @BatchLogger
    public String generateRefreshToken(UserDetails userDetails) {

        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    @BatchLogger
    public String generateRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername() + userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // this expiration is just over 24 hrs
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @BatchLogger
    public boolean isRefreshTokenValid(String jwtRefreshToken, UserDetails userDetails) {
        final String username = extractUsername(jwtRefreshToken);
        return (username.equals(userDetails.getUsername() + userDetails.getUsername()))
                && isTokenExpired(jwtRefreshToken);
    }

    @BatchLogger
    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername())) && isTokenExpired(jwtToken);
    }

    @BatchLogger
    private boolean isTokenExpired(String jwtToken) {
        return !extractExpiration(jwtToken).before(new Date());
    }

    @BatchLogger
    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    @BatchLogger
    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    @Loggable
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
