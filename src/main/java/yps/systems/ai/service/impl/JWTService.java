package yps.systems.ai.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import yps.systems.ai.model.JWTRequest;
import yps.systems.ai.service.interfaces.IJWTService;

import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JWTService implements IJWTService {

    @Override
    public Mono<String> generateJWT(String privateKeyFilePath, JWTRequest jwtRequest) {
        return this.loadKeyPairFromPEM(privateKeyFilePath)
                .handle((privateKey, sink) -> {
                    JWSHeader header = new JWSHeader
                            .Builder(JWSAlgorithm.ES256)
                            .build();
                    Instant now = Instant.now();
                    Instant expirationTime = now.plusMillis(jwtRequest.expirationTimeMillis());
                    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                            .issuer(jwtRequest.issuer())
                            .subject(jwtRequest.subject())
                            .claim("idPerson", jwtRequest.idPerson())
                            .claim("username", jwtRequest.username())
                            .claim("roles", jwtRequest.roleNames())
                            .issueTime(Date.from(now))
                            .expirationTime(Date.from(expirationTime))
                            .build();
                    SignedJWT signedJWT = new SignedJWT(header, claimsSet);
                    ECDSASigner signer;
                    try {
                        signer = new ECDSASigner((ECPrivateKey) privateKey);
                        signedJWT.sign(signer);
                    } catch (JOSEException e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    sink.next(signedJWT.serialize());
                });
    }

    private Mono<PrivateKey> loadKeyPairFromPEM(String privateKeyFilePath) {
        return Mono.fromCallable(() -> {
            try (PEMParser pemParser = new PEMParser(new FileReader(privateKeyFilePath))) {
                Object pemObject = pemParser.readObject();
                if (pemObject instanceof PEMKeyPair pemKeyPair) {
                    // Handle PEMKeyPair
                    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                    return converter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
                } else if (pemObject instanceof PrivateKeyInfo privateKeyInfo) {
                    // Handle PrivateKeyInfo
                    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                    return converter.getPrivateKey(privateKeyInfo);
                } else {
                    throw new RuntimeException("Unsupported PEM object type: " + pemObject.getClass());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading PEM file: " + e.getMessage(), e);
            }
        });
    }


}
