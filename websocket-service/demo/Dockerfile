FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /websocket-service
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /websocket-service

COPY --from=build /websocket-service/target/demo-0.0.1-SNAPSHOT.jar ./websocket-service.jar

EXPOSE 8080
CMD ["java", "-jar", "websocket-service.jar"]