FROM eclipse-temurin:21-jre

COPY *.jar /app/jwt-service.jar
COPY private_key.pem /app/private_key.pem

WORKDIR /app

CMD ["java", "-jar", "jwt-service.jar"]