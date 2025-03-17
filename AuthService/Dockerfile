FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY . .

RUN mvn clean install -pl AuthService -am -DskipTests

WORKDIR /app
COPY --from=builder /build/AuthService/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "app.jar"]
