FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy the pom.xml
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src src

# Package the application (skip tests for faster builds)
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the generated jar from the build stage
COPY --from=build /app/target/wellofast-hms-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
