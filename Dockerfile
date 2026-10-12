# Stage 1: Build stage using Maven and Eclipse Temurin JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime stage using Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user and initialize the data directory with write permissions
RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && mkdir -p /app/data \
    && chown -R appuser:appgroup /app \
    && chmod -R 775 /app/data

# Copy executable jar from builder stage
COPY --chown=appuser:appgroup --from=build /app/target/*.jar app.jar

USER appuser

# Render assigns dynamic PORT environment variable (default 8080)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
