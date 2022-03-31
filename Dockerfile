FROM maven:3.6.3-jdk-11-slim AS cache
WORKDIR /build
COPY pom.xml .
RUN mvn -T 2C dependency:go-offline --no-transfer-progress

FROM cache as test
WORKDIR /build
COPY pom.xml .
COPY src/ src/
RUN mvn test

FROM cache as builder
ARG APP_VERSION="0.0.1-beta"
WORKDIR /build
COPY pom.xml .
COPY src/ src/
RUN mvn versions:set -DnewVersion=${APP_VERSION}
RUN mvn package -DskipTests -DfinalName="app-exec"

FROM openjdk:11-jre-slim as app
LABEL maintainer="lillyneir<dev@lillyneir.hu>"
COPY --from=builder /build/target/app-exec.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]