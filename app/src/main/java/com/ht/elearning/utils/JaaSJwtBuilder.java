package com.ht.elearning.utils;

import com.ht.elearning.user.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.Map;
import java.util.Base64;

public class JaaSJwtBuilder {
    public static final String RSA = "RSA";
    public static final String EMPTY = "";
    public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FILE_RSA = System.getProperty("user.dir") + "/app/src/main/resources/jitsi.pk";

    public static RSAPrivateKey getPemPrivateKey(String filename) throws Exception {
        String pem = new String(Files.readAllBytes(Paths.get(filename)));
        String privateKey = pem.replace(BEGIN_PRIVATE_KEY, EMPTY).replace(END_PRIVATE_KEY, EMPTY).replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(privateKey);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    public static String buildJaasJwt(String jitsiAppId, String publicKey, User user, boolean isModerator, String room, Date exp, Date nbf) throws Exception {
        RSAPrivateKey rsaPrivateKey = getPemPrivateKey(PRIVATE_KEY_FILE_RSA);
        JwtBuilder builder = io.jsonwebtoken.Jwts.builder()
                .setClaims(Map.of(
                        "context", Map.of(
                                "user", Map.of(
                                        "avatar", user.getAvatarUrl(),
                                        "name", user.getFullName(),
                                        "email", user.getEmail(),
                                        "id", user.getId(),
                                        "moderator", isModerator
                                ),
                                "features", Map.of(
                                        "recording", true
                                ),
                                "room", Map.of(
                                        "regex", false
                                )
                        ),
                        "room", room
                ))
                .setAudience("jitsi")
                .setSubject(jitsiAppId)
                .setIssuer("chat")
                .setExpiration(exp)
                .setNotBefore(nbf)
                .setHeaderParams(Map.of(
                        "alg", "RS256",
                        "typ", "JWT",
                        "kid", publicKey
                ))
                .signWith(rsaPrivateKey, SignatureAlgorithm.RS256);

        return builder.compact();
    }
}
