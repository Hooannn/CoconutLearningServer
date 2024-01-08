package com.ht.elearning.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.*;

import java.io.IOException;
import java.util.List;

enum ActionType {
    PRIMARY, DANGER, DEFAULT
}

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    private String title;
    private String description;
    @JsonProperty("callback_url")
    private String callbackUrl;
    private ActionType type;
}

@Converter
class ActionListConverter implements AttributeConverter<List<Action>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Action> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Action list to JSON", e);
        }
    }

    @Override
    public List<Action> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Action>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to Action list", e);
        }
    }
}
