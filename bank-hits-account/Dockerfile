FROM eclipse-temurin:23-jre
WORKDIR /app
COPY build/libs/bank-hits-account-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]