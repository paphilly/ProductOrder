# Multi-stage Dockerfile for ProductOrder Web Server (Spring Boot)

# Stage 1: Build stage using official Maven image
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy POM file and pre-fetch dependencies to optimize layer caching
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline -B || true

# Copy source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image using Temurin JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security best practice: Create non-root system user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy packaged Spring Boot JAR from builder stage
COPY --from=builder /app/target/com.konark-0.0.1-SNAPSHOT.jar app.jar

# Adjust permissions
RUN chown -R appuser:appgroup /app
USER appuser

# Expose Spring Boot webserver port
EXPOSE 8080

# Environment variables with sensible defaults for external DB connection
ENV SERVER_PORT=8080 \
    SPRING_DATASOURCE_URL="" \
    SPRING_DATASOURCE_USERNAME="" \
    SPRING_DATASOURCE_PASSWORD="" \
    SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    SPRING_JPA_HIBERNATE_DIALECT="org.hibernate.dialect.SQLServer2012Dialect"

# Launch application
ENTRYPOINT ["java", "-jar", "app.jar"]
