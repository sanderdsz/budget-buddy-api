FROM maven:3.8.4-openjdk-17 AS build
COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package -DskipTests


FROM openjdk:17-jdk-slim-buster
COPY --from=build /app/target/*.jar /app/budget-buddy-api-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT  ["java", "-jar", "/app/budget-buddy-api-0.0.1-SNAPSHOT.jar"]