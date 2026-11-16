# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY services/agent-gateway/pom.xml ./
RUN mvn -q -e -B dependency:go-offline
COPY services/agent-gateway/src ./src
RUN mvn -q -e -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/agent-gateway-0.1.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
