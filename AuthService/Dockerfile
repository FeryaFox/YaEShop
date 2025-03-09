FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# 1) Скопировать корневой pom.xml (где <modules> ... )
COPY pom.xml .

# 2) Скопировать POM и исходники всех модулей
# (Если директория не нужна — можно скопировать просто пустую)
COPY AuthService/pom.xml AuthService/pom.xml
COPY AuthService/ AuthService/

COPY shared-library/pom.xml shared-library/pom.xml
COPY shared-library/ shared-library/

COPY EurekaService/pom.xml EurekaService/pom.xml
COPY EurekaService/ EurekaService/

COPY GatewayService/pom.xml GatewayService/pom.xml
COPY GatewayService/ GatewayService/

# ... и так далее для всех модулей, чтобы не было ошибок,
# что "Child module ... does not exist".

# 3) Собираем только AuthService (+ зависимый shared-library)
RUN mvn clean install -pl AuthService -am -DskipTests

# -- Финальный образ --
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/AuthService/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
