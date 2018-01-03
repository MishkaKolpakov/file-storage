package ua.softserve.academy.kv030.authservice.utils;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.softserve.academy.kv030.authservice.exceptions.InvalidTokenException;
import ua.softserve.academy.kv030.authservice.values.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is utility for work with JSON Web Token
 */
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.life_minutes}")
    private int jwtLifeMinutes;

    @Value("${jwt.refreshing_minutes}")
    private int jwtRefreshingMinutes;

    @Autowired
    private Logger logger;

    /**
     * Create {@link Authentication} based on received JWT information
     *
     * @param token JSON Web Token
     * @return {@link Authentication}
     * @throws InvalidTokenException
     *
     * @see Authentication
     */
    public Authentication getAuthentication(String token) throws InvalidTokenException {

        Claims claims = getClaims(token);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class)));
        logger.debug(String.format("Adding authorities %s for %s", authorities.toString(), claims.getSubject()));


        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }

    /**
     * Refresh token if it'll expire soon, return old token otherwise
     *
     * @param oldToken JSON Web Token
     * @return  JSON Web Token
     * @throws InvalidTokenException
     */
    public String refresh(String oldToken) throws InvalidTokenException {

        logger.debug("Refreshing JWT");
        Claims oldClaims = getClaims(oldToken);

        Date expiration = oldClaims.getExpiration();
        logger.debug("JWT expiration date: " + expiration);

        Date refreshingStart = DateUtils.addMinutes(expiration, -jwtRefreshingMinutes);
        logger.debug("Refreshing start date for current JWT: " + refreshingStart);

        return  (new Date().after(refreshingStart))
                ? generateToken(oldClaims.getSubject(), oldClaims.get("role", String.class))
                : oldToken;
    }

    /**
     * Generate JSON Web Token from user's email and role
     *
     * @param email user email
     * @param role user role
     * @return JSON Web Token
     */
    public String generateToken(String email, String role) {

        logger.debug(String.format("Generating token for %s with role %s", email, role));

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setExpiration(generateExpDate())
                .signWith(Constants.jwtSignatureAlgorithm, jwtSecret)
                .compact();
    }

    /**
     * Validate JSON Web Token (The token should be not <code>null</code>,
     * unsigned correctly with secret, be not expired)
     *
     * @param token JSON Web Token
     * @return  <code>true</code> if token is valid,
     *          <code>false</code> otherwise
     */
    public boolean isValid(String token) {
        try {
            getClaims(token);
        } catch (InvalidTokenException e) {
            return false;
        }
        return true;
    }

    private Date generateExpDate() {
        return new Date(System.currentTimeMillis() + jwtLifeMinutes * 60 * 1000);
    }

    private Claims getClaims(String token) throws InvalidTokenException {
        try {
            logger.debug("Parsing JWT");
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException | IllegalArgumentException  e) {
            logger.error(e.getMessage(), e);
            throw new InvalidTokenException();
        }
    }

}
