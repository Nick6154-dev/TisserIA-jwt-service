FROM eclipse-temurin:21-jre

COPY *.jar /app/jwt-service.jar

WORKDIR /app

CMD ["java", "-jar", "jwt-service.jar"]