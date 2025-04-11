# --- STAGE 1: Build using official Gradle image with non-root user ---
    FROM gradle:8.13.0-jdk17-corretto AS build
    WORKDIR /home/gradle/app
    COPY --chown=gradle:gradle . .
    RUN gradle build -x test --no-daemon
    
    # --- STAGE 2: Run with slim JRE and non-root user ---
    FROM eclipse-temurin:24-jre-alpine
    
    # Add non-root user for running the app securely
    RUN addgroup -S appgroup && adduser -S appuser -G appgroup
    
    WORKDIR /app
    COPY --from=build /home/gradle/app/build/libs/*.jar app.jar
    
    # Switch to non-root user
    USER appuser
    
    # Expose app port (optional)
    EXPOSE 8080
    
    ENTRYPOINT ["java", "-jar", "app.jar"]
    