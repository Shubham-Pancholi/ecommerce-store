# 1. Use a tiny Alpine Linux image with Java 21 pre-installed
FROM eclipse-temurin:21-jdk-jammy

# 2. Create a folder inside the container called /app
WORKDIR /app

# 3. Copy our compiled Spring Boot JAR file into the container
COPY target/*.jar app.jar

# 4. Expose port 8080 so traffic can reach it
EXPOSE 8080

# 5. Tell the container what to do when it starts
ENTRYPOINT ["java", "-jar", "app.jar"]