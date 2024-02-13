package yps.systems.ai.service.interfaces;

import reactor.core.publisher.Mono;
import yps.systems.ai.model.JWTRequest;

import java.util.List;

public interface IJWTService {

    Mono<String> generateJWT(String privateKeyPEM, JWTRequest jwtRequest);

}
