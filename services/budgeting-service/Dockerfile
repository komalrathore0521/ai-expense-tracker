# STAGE 1: Build the application using Maven
FROM openjdk:17-jdk-slim as builder

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and the pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Package the application into a JAR file, skipping the tests
RUN ./mvnw package -DskipTests


# STAGE 2: Create the final, smaller image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy only the built JAR file from the 'builder' stage
COPY --from=builder /app/target/budgeting-service-0.0.1-SNAPSHOT.jar .

# Expose the port the application will run on (defined in application.properties)
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "budgeting-service-0.0.1-SNAPSHOT.jar"]
