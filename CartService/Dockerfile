FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Копируем весь проект целиком:
COPY . .

# Выполняем сборку конкретного модуля AuthService с учётом всех зависимых модулей:
RUN mvn clean install -pl CartService -am -DskipTests

# -- Финальный образ --
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/CartService/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "app.jar"]
