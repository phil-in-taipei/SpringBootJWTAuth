package JWTDockerTutorial.security.config;

import JWTDockerTutorial.security.services.auth.JwtService;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    //private final UserDetailsService userDetailsService;
    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull  HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("It is null or has different header");
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwtToken = authHeader.substring(7); // after the "Bearer " prefix
            System.out.println("This is the jwtToken in the auth filter class : " + jwtToken);
            username = jwtService.extractUsername(jwtToken);
            System.out.println("This is the username in the auth filter class: " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("The user is not logged in (auth filter class if clause)");
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("These are the user details (in the auth filter): " + userDetails);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    System.out.println("This is the token that has been generated (auth filter): " + authToken);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (SignatureException  | MalformedJwtException | UnsupportedJwtException e) {
            System.out.println("There has been an error");
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().print("{ \"Message\": \"Authorization error\" }");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }  catch (ExpiredJwtException e) {
            System.out.println("There has been an error");
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().print("{ \"Message\": \"Session Expired. Please login again\" }");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }  catch (IllegalArgumentException e) {
            System.out.println("There has been an error");
            System.out.println(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().print("{ \"Message\": \"User is unauthorized!\" }");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }
    }
}
