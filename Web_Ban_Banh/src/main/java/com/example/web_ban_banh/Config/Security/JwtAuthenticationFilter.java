package com.example.web_ban_banh.Config.Security;

import com.example.web_ban_banh.Exception.Internal_Server_ErrorEX_500.InternalServerErrorExceptionCustom;
import com.example.web_ban_banh.Service.BlacklistService.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUltil;
    private final UserDetailsService userDetailsService;
    private TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtil jwtUltil, UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUltil = jwtUltil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token=null ;
        String username=null ;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            // Kiểm tra token trong blacklist
            if (tokenBlacklistService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã bị thu hồi. Hãy đăng nhập lại");
                return; // Dừng xử lý ngay lập tức
            }

            try {
                username = jwtUltil.extractUsername(token);
                // Kiểm tra token có hết hạn hay không
                Date expiration = jwtUltil.extractExpiration(token);
                if (expiration.before(new Date())) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Đã hết hạn token, hãy đăng nhập lại");
                    return; // Dừng xử lý ngay lập tức
                }
            } catch (JwtException e) {
                // Bắt các ngoại lệ liên quan đến JWT, bao gồm ExpiredJwtException
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Đã hết hạn token, hãy đăng nhập lại");
                return; // Dừng xử lý ngay lập tức
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetail = userDetailsService.loadUserByUsername(username);

            if (jwtUltil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
