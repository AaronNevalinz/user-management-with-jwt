package com.user.jwt;

import com.user.services.CustomUserDetailsService;
import com.user.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter { // Extending with this, ensures the filter once per request
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        extract JWT token from authorization header
        String token = getTokenFromRequest(request);

        if (token != null && jwtUtils.validateToken(token)) {
//            Extract username from token
            String username = jwtUtils.getUsernameFromToken(token);
            System.out.println(username);
//            Load user from db
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println(userDetails);

//            Create authentication token and set it in security context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(authentication);
        }
        filterChain.doFilter(request, response);

    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
