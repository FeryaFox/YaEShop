package ru.feryafox.kafka.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomKafkaDeserializer implements Deserializer<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Class<?>> typeMapping = new HashMap<>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        log.info("Конфигурация десериализатора Kafka...");

        configs.forEach((key, value) -> {
            if (key.startsWith("event.class.")) {
                String eventType = key.replace("event.class.", "");
                Class<?> eventClass = getClass(value);
                if (eventClass != null) {
                    typeMapping.put(eventType, eventClass);
                    log.info("Добавлен маппинг события: {} -> {}", eventType, eventClass.getName());
                }
            }
        });

        if (typeMapping.isEmpty()) {
            log.error("Не загружено ни одного типа событий! Проверь конфигурацию.");
            throw new RuntimeException("Не загружено ни одного типа событий! Проверь конфигурацию.");
        }

        log.info("Десериализатор Kafka успешно сконфигурирован. Загружено {} типов событий.", typeMapping.size());
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            JsonNode jsonNode = objectMapper.readTree(data);

            if (!jsonNode.has("type")) {
                log.warn("Поле 'type' отсутствует в JSON: {}", jsonNode);
                throw new IllegalArgumentException("Поле 'type' отсутствует в JSON");
            }

            String type = jsonNode.get("type").asText();
            Class<?> targetClass = typeMapping.get(type);

            if (targetClass == null) {
                log.warn("Неизвестный тип сообщения: {}", type);
                throw new IllegalArgumentException("Неизвестный тип сообщения: " + type);
            }

            Object deserializedObject = objectMapper.treeToValue(jsonNode, targetClass);
            log.info("Успешная десериализация сообщения типа {} для топика {}", type, topic);
            return deserializedObject;

        } catch (IOException e) {
            log.error("Ошибка при разборе JSON в топике {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Ошибка десериализации JSON", e);
        } catch (Exception e) {
            log.error("Ошибка десериализации в топике {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Ошибка десериализации сообщения", e);
        }
    }

    @Override
    public void close() {
        log.info("Закрытие десериализатора Kafka...");
    }

    private Class<?> getClass(Object className) {
        try {
            return className != null ? Class.forName((String) className) : null;
        } catch (ClassNotFoundException e) {
            log.error("Класс не найден: {}", className, e);
            throw new RuntimeException("Класс не найден: " + className, e);
        }
    }
}
