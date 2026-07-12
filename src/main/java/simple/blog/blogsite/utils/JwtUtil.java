package simple.blog.blogsite.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "Like, Share and Subscribe to Code io - Tamil, Our simple and easy to use IDE";
    private final long EXPIRATION = 1000*60*60*10; //validation for token: 1 hour
    private final Key secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey,SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token){
        String e=Jwts.parser()           //parser() instead of parserBuider()
                .setSigningKey(secretKey) //verifyWith(secretkey) instead of setSigningKey(secretkey) in jjwt version 0.12.0+
                .build()
                .parseSignedClaims(token) //parseSignedClaims(token) instead of parseClaimsJws(token) in jjwt version 0.12.0+
                .getPayload() //getPayload() instead of getBody() in jjwt version 0.12.0+
                .getSubject();
        System.out.println(e);
        return e;
    }
    public boolean validateJwtToken(String token){
        try{
            extractEmail(token);
            return true;
        }
        catch(JwtException exception){
            return false;
        }
    }
}

