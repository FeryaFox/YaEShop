FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .

COPY GatewayService/pom.xml GatewayService/pom.xml

RUN mvn dependency:go-offline -B -f GatewayService/pom.xml

COPY . .

WORKDIR /app/GatewayService
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/GatewayService/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
