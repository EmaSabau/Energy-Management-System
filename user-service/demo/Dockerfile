# Etapa de build cu Maven și Java 21
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /users-service
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src/ ./src
RUN mvn clean install -DskipTests

# Etapa de runtime cu Java 21 LTS
FROM eclipse-temurin:21-jdk
WORKDIR /users-service

# Copiem jar-ul construit anterior
COPY --from=build /users-service/target/demo-0.0.1-SNAPSHOT.jar /users-service/users-service.jar

EXPOSE 8080
CMD ["java", "-jar", "users-service.jar"]