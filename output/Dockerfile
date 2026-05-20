#Cборка
FROM gradle:8.5-jdk17 AS builder

WORKDIR /build
COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/
RUN gradle dependencies --no-daemon
COPY src/ ./src/
RUN gradle build -x test --no-daemon
RUN find /build/build/libs -type f -name "*.jar" ! -name "*-plain.jar" -exec mv {} /build/build/libs/application.jar \;

#Запуск
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /build/build/libs/application.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]