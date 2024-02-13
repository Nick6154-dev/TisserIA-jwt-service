package yps.systems.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import yps.systems.ai.model.JWTRequest;
import yps.systems.ai.service.interfaces.IJWTService;

@RestController
@RequestMapping("/jwtService")
public class JwtController {

    @Autowired
    private IJWTService service;

    @Value("${env.file.privateKeyPathPEM}")
    private String privateKeyPathPEM;

    @PostMapping("/buildJWT")
    public Mono<String> buildJWT(@RequestBody JWTRequest jwtRequest) {
        return service.generateJWT(privateKeyPathPEM, jwtRequest)
                .onErrorResume(error -> {
                    System.out.println(error.getMessage());
                    return Mono.just("Error building jwt.");
                });
    }

}
