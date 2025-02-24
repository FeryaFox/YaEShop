package ru.feryafox.kafka.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.HashMap;
import java.util.Map;

public class CustomKafkaDeserializer implements Deserializer<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Class<?>> typeMapping = new HashMap<>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        configs.forEach((key, value) -> {
            if (key.startsWith("event.class.")) {
                String eventType = key.replace("event.class.", "");
                Class<?> eventClass = getClass(value);
                typeMapping.put(eventType, eventClass);
            }
        });

        if (typeMapping.isEmpty()) {
            throw new RuntimeException("Не загружено ни одного типа событий! Проверь конфигурацию.");
        }
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            JsonNode jsonNode = objectMapper.readTree(data);

            if (!jsonNode.has("type")) {
                throw new IllegalArgumentException("Поле 'type' отсутствует в JSON: " + jsonNode);
            }

            String type = jsonNode.get("type").asText();
            Class<?> targetClass = typeMapping.get(type);

            if (targetClass == null) {
                throw new IllegalArgumentException("Неизвестный тип сообщения: " + type);
            }

            return objectMapper.treeToValue(jsonNode, targetClass);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации JSON", e);
        }
    }

    @Override
    public void close() {

    }

    private Class<?> getClass(Object className) {
        try {
            return className != null ? Class.forName((String) className) : null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Класс не найден: " + className, e);
        }
    }
}
