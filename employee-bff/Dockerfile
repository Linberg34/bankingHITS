FROM eclipse-temurin:21-jre

WORKDIR /app

# Собранный Spring Boot jar (Gradle кладёт его в build/libs)
COPY build/libs/employee-bff-*.jar app.jar

# Внутри контейнера BFF слушает на 8084 (как в application.properties)
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

#FROM eclipse-temurin:21-jre AS build
#WORKDIR /app
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#COPY src src
#RUN chmod +x gradlew
#RUN ./gradlew bootJar -x test
#
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
#EXPOSE 8085
## профиль передаётся через переменную окружения
#ENTRYPOINT ["java", "-jar", "app.jar"]