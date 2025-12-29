FROM maven:3.9-eclipse-temurin-21 as build
WORKDIR /workspace
COPY integrationtest/self-healing-integration-tests/pom.xml ./
RUN mvn -q -B dependency:go-offline
COPY integrationtest/self-healing-integration-tests/src ./src
RUN mvn -q -B package -DskipTests

FROM maven:3.9-eclipse-temurin-21
WORKDIR /workspace
COPY --from=build /workspace .
ENV BASE_URL=http://frontend BACKEND_URL=http://backend:8080 REMOTE_WEBDRIVER_URL=http://selenium:4444/wd/hub
CMD ["mvn","-q","test","-DskipITs=false"]
