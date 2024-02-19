package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.logging.BatchLogger;
import JWTDockerTutorial.security.logging.Loggable;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    // DO NOT put visible key string here in actual apps!!
    private static final String SECRET_KEY = "050b872ced72e5c140063eefc0cb17b7050b872ced72e5c140063eefc0cb17b7";

    @BatchLogger
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @BatchLogger
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
       // System.out.println("****Extracting claim***");
        Claims claims = extractAllClaims(jwtToken);
        //System.out.println("These are the claims: " + claims);
        return claimsResolver.apply(claims);
    }

    @BatchLogger
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

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
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
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
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @BatchLogger
    public boolean isRefreshTokenValid(
            String jwtRefreshToken, UserDetails userDetails
    ) throws ExpiredJwtException {
        //System.out.println("Calling method to check if refresh token is valid");
        try {
            String username = extractUsername(jwtRefreshToken);
           // System.out.println("This is the name in the refresh token " + username);
            //System.out.println(userDetails.getUsername() + userDetails.getUsername());
            return username.equals(userDetails.getUsername() + userDetails.getUsername());
        } catch (ExpiredJwtException e) {
            //System.out.println("Refresh token is expired");
            return false;
        }

    }

    @BatchLogger
    public boolean isTokenValid(String jwtToken, UserDetails userDetails)
            throws ExpiredJwtException {
        try {
            String username = extractUsername(jwtToken);
            return username.equals(userDetails.getUsername());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /* note: the workflow below will raise an error when expired -- not return boolean false
    @BatchLogger
    private boolean isTokenExpired(String jwtToken) {
        System.out.println("Checking if token is expired");
        return extractExpiration(jwtToken).before(new Date());
    }

    @BatchLogger
    private Date extractExpiration(String jwtToken) {
        System.out.println("Extract exp method calling....");
        return extractClaim(jwtToken, Claims::getExpiration);
    }
     */

    @BatchLogger
    private Claims extractAllClaims(String jwtToken) {
        System.out.println("Extract all claims");
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    @BatchLogger
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
