package com.monorama.iot_server.security.apple;

import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    @Value("${apple.audience}")
    private String issuer;
    @Value("${apple.public-key-url}")
    private String publicKeyUrl;

    public String verifyAndGetUserId(String identityToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(identityToken);

            URI uri = URI.create(publicKeyUrl);
            JWKSet jwkSet = JWKSet.load(uri.toURL());

            List<JWK> keys = jwkSet.getKeys();

            String kid = signedJWT.getHeader().getKeyID();
            String alg = signedJWT.getHeader().getAlgorithm().getName();
            JWK jwk = keys.stream()
                    .filter(k -> k.getKeyID().equals(kid) && k.getAlgorithm().getName().equals(alg))
                    .findFirst()
                    .orElseThrow(() -> new CommonException(ErrorCode.NO_MATCH_APPLE_PUBLIC_KEY_ERROR));

            RSAKey rsaKey = (RSAKey) jwk;
            if (!signedJWT.verify(new RSASSAVerifier(rsaKey.toRSAPublicKey()))) {
                throw new CommonException(ErrorCode.INVALID_APPLE_TOKEN_SIGNATURE_ERROR);
            }

            JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
            if (!issuer.equals(claimSet.getIssuer())) {
                throw new CommonException(ErrorCode.INVALID_APPLE_ISSUER_ERROR);
            }

            if (claimSet.getExpirationTime().before(new Date())) {
                throw new CommonException(ErrorCode.APPLE_IDENTITY_TOKEN_EXPIRED_ERROR);
            }

            return claimSet.getSubject();
        } catch (ParseException | JOSEException | java.io.IOException e) {
            throw new CommonException(ErrorCode.APPLE_IDENTITY_TOKEN_VERIFICATION_ERROR);
        }
    }
}
