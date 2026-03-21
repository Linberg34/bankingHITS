FROM eclipse-temurin:21-jre

WORKDIR /app

# Собранный Spring Boot jar (Gradle кладёт его в build/libs)
COPY build/libs/employee-bff-*.jar app.jar

# Внутри контейнера BFF слушает на 8084 (как в application.properties)
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

