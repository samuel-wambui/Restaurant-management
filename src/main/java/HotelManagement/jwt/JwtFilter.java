package HotelManagement.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        // 1) Only proceed if we have a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        // If no token, just continue as anonymous
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String username;
        try {
            // 2) Attempt to extract the username (this will throw if token invalid/expired)
            username = jwtService.extractUserName(token);
        } catch (JwtException e) {
            // 3) Malformed or expired token → 401
            logger.warn("JWT parsing failed: {}");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
            return;
        }

        // 4) If not yet authenticated in this context, validate & set auth
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load full details (roles, etc.) from your UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Standard user authentication flow
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5) If you have custom “clientType” roles embedded in the token, you can override/add them here
        String clientType = jwtService.extractClaim(token, claims -> claims.get("clientType", String.class));
        if (clientType != null &&
                (clientType.equals("PARTNER") || clientType.equals("SYSTEM"))) {

            List<SimpleGrantedAuthority> extraRoles =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + clientType));

            UsernamePasswordAuthenticationToken clientAuth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            extraRoles
                    );
            clientAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(clientAuth);
        }

        // 6) Finally, continue the chain exactly once
        filterChain.doFilter(request, response);
    }
}
