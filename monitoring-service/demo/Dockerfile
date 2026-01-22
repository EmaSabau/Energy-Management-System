FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /monitoring-service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /monitoring-service

COPY --from=build /monitoring-service/target/demo-0.0.1-SNAPSHOT.jar ./monitoring-service.jar

EXPOSE 8080
CMD ["java", "-jar", "monitoring-service.jar"]