# STAGE 1: Build the application JAR
# Use an official OpenJDK runtime as a parent image.
# This stage is named 'builder' so we can reference it later.
FROM openjdk:17-jdk-slim as builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and the POM file
# This is done in a separate step to leverage Docker's layer caching.
# If these files don't change, Docker will reuse the cached layer.
COPY ./mvnw .
COPY ./.mvn ./.mvn
COPY ./pom.xml .

# Download the project dependencies
# This is also a separate layer. If dependencies in pom.xml don't change,
# this layer will be cached, speeding up future builds.
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY ./src ./src

# Package the application into a JAR file, skipping the tests
RUN ./mvnw package -DskipTests

# STAGE 2: Create the final, smaller image
# Start from a fresh, slim Java image. This final image will not contain
# the Maven wrapper, source code, or any build tools.
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy only the built JAR file from the 'builder' stage
COPY --from=builder /app/target/transaction-service-0.0.1-SNAPSHOT.jar .

# Expose the port the application will run on (defined in application.properties)
EXPOSE 8081

# The command to run the application
ENTRYPOINT ["java", "-jar", "transaction-service-0.0.1-SNAPSHOT.jar"]

