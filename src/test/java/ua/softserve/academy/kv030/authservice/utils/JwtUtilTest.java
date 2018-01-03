package ua.softserve.academy.kv030.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import ua.softserve.academy.kv030.authservice.exceptions.InvalidTokenException;
import ua.softserve.academy.kv030.authservice.values.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String email = "pupkin@example.com";
    private static final String role = "USER";

    @Test(expected = InvalidTokenException.class)
    public void getAuthentication_ShouldThrowExceptionForExpiredToken() throws Exception {
        Date expirationDate = new Date(System.currentTimeMillis() - 2_000);

        jwtUtil.getAuthentication(createToken(email, role, expirationDate));
    }

    @Test
    public void getAuthentication_ShouldCreateAuthenticationForCorrectToken() throws Exception {
        Date expirationDate = new Date(System.currentTimeMillis() + 2_000);

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorityList);

        Authentication authFromJWT = jwtUtil.getAuthentication(createToken(email, role, expirationDate));

        assertEquals(auth, authFromJWT);
    }


    @Test
    public void refresh_ShouldReturnTokenWithSameCredentials() throws Exception {
        Date oldExpiredDate = new Date(System.currentTimeMillis() + 2_000);

        String tokenToRefresh = createToken(email, role, oldExpiredDate);
        String refreshed = jwtUtil.refresh(tokenToRefresh);

        assertCredentials(email, role, refreshed);
    }

    @Test(expected = InvalidTokenException.class)
    public void refresh_ShouldThrowExceptionForExpiredToken() throws Exception {
        Date oldExpiredDate = new Date(System.currentTimeMillis() - 2_000);

        String tokenToRefresh = createToken(email, role, oldExpiredDate);
        jwtUtil.refresh(tokenToRefresh);
    }

    @Test
    public void generateToken_ShouldGenerateTokenWithSpecifiedCredentials() throws Exception {
        assertCredentials(email, role, jwtUtil.generateToken(email, role));
    }

    public void assertCredentials(String email, String role, String tokenToCheck) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(tokenToCheck)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertEquals(role, claims.get("role", String.class));
    }

    @Test
    public void isValid_ShouldReturnTrueForValidToken() throws Exception {
        String validToken = createToken(email, role, new Date(System.currentTimeMillis() + 2_000));

        assertTrue(jwtUtil.isValid(validToken));
    }

    @Test
    public void isValid_ShouldReturnFalseForExpiredToken() throws Exception {
        String expiredToken = createToken(email, role, new Date(System.currentTimeMillis() - 2_000));

        assertFalse(jwtUtil.isValid(expiredToken));
    }

    @Test
    public void isValid_ShouldReturnFalseForInvalidSomething() throws Exception {
        String notToken = "Invalid.Something._";

        assertFalse(jwtUtil.isValid(notToken));
    }

    private String createToken(String mail, String role, Date expirationDate) {
        return Jwts.builder()
                .setSubject(mail)
                .claim("role", role)
                .setExpiration(expirationDate)
                .signWith(Constants.jwtSignatureAlgorithm, jwtSecret)
                .compact();
    }

}