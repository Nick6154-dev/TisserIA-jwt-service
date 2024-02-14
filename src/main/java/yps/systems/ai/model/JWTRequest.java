package yps.systems.ai.model;

import java.util.List;

public record JWTRequest(String issuer, String subject, Long idPerson, String name, String lastname, String username,
                         List<String> roleNames, long expirationTimeMillis) {
}
