//package com.example.web_ban_banh.Config.Security;
//
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private final String SECRET_KEY;
//    private final long jwtExpiration;
//
//    public JwtUtil( @Value("${jwt.secret}") String SECRET_KEY,@Value("${jwt.expiration}") long jwtExpiration) {
//        this.SECRET_KEY = SECRET_KEY;
//        this.jwtExpiration = jwtExpiration;
//    }
//
//    private Key getsigningKey(){
//      return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//    };
//
//    public String generaToken(String username){
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
//                .signWith(getsigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String extractUsername(String token){
//        return Jwts.parserBuilder()
//                .setSigningKey(getsigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validateToken(String token){
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getsigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        }catch(JwtException e){
//            return false;
//        }
//    }
//}
