FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY . .

RUN mvn clean install -pl ProductService -am -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/ProductService/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "app.jar"]
