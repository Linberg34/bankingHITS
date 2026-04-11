FROM eclipse-temurin:23-jre

WORKDIR /app

COPY build/libs/monitoring-service-*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
