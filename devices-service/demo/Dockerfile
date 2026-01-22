# Etapa de build cu Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /devices-service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean install -DskipTests

# Etapa de runtime cu Java 21 LTS
FROM eclipse-temurin:21-jdk
WORKDIR /devices-service

# Copiem jar-ul construit anterior
COPY --from=build /devices-service/target/demo-0.0.1-SNAPSHOT.jar ./devices-service.jar

EXPOSE 8080
CMD ["java", "-jar", "devices-service.jar"]