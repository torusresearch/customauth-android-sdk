package org.torusresearch.customauth.app;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {
    public static String generateIdToken(String email) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .setSubject("email|" + email.split("@")[0])
                .setAudience("torus-key-test")
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
                .setIssuedAt(new Date())
                .setIssuer("torus-key-test")
                .claim("email", email)
                .claim("nickname", email.split("@")[0])
                .claim("name", email)
                .claim("picture", "")
                .claim("email_verified", true)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256).compact();
    }

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byteArray = {48, 65, 2, 1, 0, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2,
                1, 6, 8, 42, -122, 72, -50, 61, 3, 1, 7, 4, 39, 48, 37, 2, 1, 1, 4, 32, -125,
                -18, -126, -21, 112, -90, -98, -6, 53, 89, 60, 108, 121, -40, 38, -1, -108, -88,
                93, 43, 26, 87, -114, 95, 94, 24, -42, -75, 50, 86, 57, -20};
        KeyFactory kf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(byteArray);
        return kf.generatePrivate(keySpec);
    }
}
