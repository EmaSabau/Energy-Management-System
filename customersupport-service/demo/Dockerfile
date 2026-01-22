# Folosim o imagine Maven cu Java 21 pentru build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /customersupport-service
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean install -DskipTests
FROM eclipse-temurin:21-jdk
WORKDIR /customersupport-service
COPY --from=build /customersupport-service/target/demo-0.0.1-SNAPSHOT.jar ./customersupport-service.jar

EXPOSE 8080
CMD ["java", "-jar", "customersupport-service.jar"]