# Dockerfile

# --- Stage 1: Build the application ---
# We use a base image that has the Java Development Kit (JDK) and Gradle.
FROM gradle:8.5.0-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle build files first to leverage Docker's layer caching.
# If these files don't change, Docker won't re-download dependencies.
COPY build.gradle.kts settings.gradle.kts ./

# Copy the rest of the source code
COPY src ./src

# Run the Gradle 'installDist' task. This compiles the code and gathers
# all necessary JAR files and launch scripts into 'build/install/'.
RUN gradle installDist

# --- Stage 2: Create the final, lightweight image ---
# We use a smaller base image with only the Java Runtime Environment (JRE),
# as we no longer need the full JDK or Gradle to run the app.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the compiled application from the 'builder' stage
COPY --from=builder /app/build/install/Kotlin-CLI ./

# Set the ENTRYPOINT. This is the command that will be executed when
# the container starts. We point it to the launch script created by Gradle.
# The arguments to 'docker run' will be appended to this command.
ENTRYPOINT ["/app/bin/Kotlin-CLI"]