# Start with a base Java image containing the necessary runtime environment (JDK 17)
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the target directory into the container's app directory.
# The name MUST exactly match the file you built: gatepass-system-0.0.1-SNAPSHOT.jar
# This creates a file named 'app.jar' inside the container.
COPY target/gatepass-system-0.0.1-SNAPSHOT.jar app.jar

# Command to run the application when the container starts
# The jar file runs on the default Spring Boot port 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port Spring Boot runs on (optional, but good practice)
EXPOSE 8080