# Use an official Maven image to build the project
FROM maven:3.9.6-eclipse-temurin-17 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies first for faster builds
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project source code
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# Use a smaller JDK image to run the app
FROM adoptopenjdk/openjdk14
#FROM eclipse-temurin:17-jre

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
EXPOSE 80
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-ea","-jar", "app.jar"]