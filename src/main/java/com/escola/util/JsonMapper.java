package com.escola.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;

/**
 * Utility class for JSON serialization and deserialization using Jackson.
 * This class is final as it only contains static utility methods.
 *
 * @version 1.0
 */
public final class JsonMapper {
    private static final ObjectMapper objectMapper = createObjectMapper();

    private JsonMapper() {} // Private constructor for utility class

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // For Java 8 Date/Time types like LocalDate
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Serializes an object to its JSON string representation.
     *
     * @param object The object to serialize.
     * @return The JSON string.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Deserializes a JSON string to an object of the specified class.
     *
     * @param jsonString The JSON string to deserialize.
     * @param clazz      The class of the object to create.
     * @param <T>        The type of the object.
     * @return The deserialized object.
     * @throws IOException if an error occurs during deserialization.
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) throws IOException {
        return objectMapper.readValue(jsonString, clazz);
    }

    /**
     * Deserializes a JSON array string into a list of specified type.
     *
     * @param jsonArray The JSON array string.
     * @param clazz     The class of the elements in the list.
     * @param <T>       The type of the elements.
     * @return A list of deserialized objects.
     * @throws IOException if an error occurs during deserialization.
     */
    public static <T> List<T> fromJsonList(String jsonArray, Class<T> clazz) throws IOException {
        return objectMapper.readValue(jsonArray, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
}
