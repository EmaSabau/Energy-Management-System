# Etapa de build cu Java 21
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /auth-service
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean install -DskipTests

# Etapa de runtime cu Java 21 LTS
FROM eclipse-temurin:21-jdk
WORKDIR /auth-service

# Copiem jar-ul construit (asigură-te că numele jar-ului din target coincide)
COPY --from=build /auth-service/target/demo-0.0.1-SNAPSHOT.jar /auth-service/auth-service.jar

EXPOSE 8080
CMD ["java", "-jar", "auth-service.jar"]