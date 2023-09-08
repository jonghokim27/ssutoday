/**
 * @filename    : TokenProvider.java
 * @description : Provider for access/refresh token
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider;

import io.jsonwebtoken.*;
import kr.ac.ssu.ssutoday.vo.JWTPayloadVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {
    /**
     * JWT secret key from environment variable
     */
    @Value("${spring.jwt.secret}")
    private String JWT_SECRET_KEY;

    /**
     * Expire time for access token
     */
    private final Long ACCESS_TOKEN_EXP = 1000L * 60 * 60 * 2;

    /**
     * Generate access token
     * @param jwtPayloadVO jwt token payload
     * @return generated access token
     * @author jonghokim27
     */
    @NotNull
    public String generateAccessToken(@NotNull JWTPayloadVo jwtPayloadVO) {
        Claims claims = Jwts.claims();
        claims.put("studentId", jwtPayloadVO.getStudentId());
        claims.put("name", jwtPayloadVO.getName());
        claims.put("major", jwtPayloadVO.getMajor());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration((new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP)))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
    }

    /**
     * Generate random hash token
     * @return generated random hash token
     * @author jonghokim27
     */
    @Nullable
    public String generateRandomHashToken() {
        try {
            final String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_=+[]()|`~";

            final SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            String randomString = secureRandom
                    .ints(50, 0, chars.length())
                    .mapToObj(chars::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            randomString += System.currentTimeMillis();

            String token;
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(randomString.getBytes(StandardCharsets.UTF_8));
            token = String.format("%0128x", new BigInteger(1, digest.digest()));

            return token;
        }
        catch(Exception e){
            log.error("Error generating random hash token.", e);
            return null;
        }
    }

    /**
     * Validates access token
     * @param token access token
     * @return jwt token payload (JWTPayloadVO)
     * @throws BadCredentialsException thrown when access token is invalid
     * @throws ExpiredJwtException thrown when access token is expired
     * @author jonghokim27
     */
    @NotNull
    public JWTPayloadVo validateAccessToken(@NotNull String token) throws BadCredentialsException, ExpiredJwtException{
        Claims claims;
        Integer studentId;
        String name;
        String major;

        try {
            claims = Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).getBody();
            studentId = (Integer) claims.get("studentId");
            name = (String) claims.get("name");
            major = (String) claims.get("major");
        } catch (SignatureException e) {
            throw new BadCredentialsException("Invalid JWT_SECRET_KEY.", e);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid access token.", e);
        }

        return JWTPayloadVo.builder()
                .studentId(studentId)
                .name(name)
                .major(major)
                .build();

    }


}
