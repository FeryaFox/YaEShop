FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем родительский POM из корня проекта
COPY pom.xml .

# Копируем POM модуля (EurekaService) в соответствующую директорию
COPY EurekaService/pom.xml EurekaService/pom.xml

# Скачиваем зависимости для модуля EurekaService (при этом Maven увидит и родительский pom.xml)
RUN mvn dependency:go-offline -B -f EurekaService/pom.xml

# Теперь копируем весь проект, чтобы собрать исходники
COPY . .

# Переходим в папку EurekaService и собираем сервис
WORKDIR /app/EurekaService
RUN mvn clean package -DskipTests

# Финальный образ для запуска
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/EurekaService/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
