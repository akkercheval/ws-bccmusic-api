# Stage 1: Build with Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven files first for better layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml unchanged)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests --no-transfer-progress

# Stage 2: Lightweight runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for better security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy only the executable JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Production-ready JVM flags for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-jar", \
    "app.jar"]