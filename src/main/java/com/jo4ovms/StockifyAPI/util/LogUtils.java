package com.jo4ovms.StockifyAPI.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import org.springframework.stereotype.Component;

@Component
public class LogUtils {
    private final ObjectMapper objectMapper;

    public LogUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Error serializing object";
        }
    }

    public void populateLog(LogDTO logDTO, String entity, Long entityId, String operationType, Object newValue, Object oldValue, String details) {
        logDTO.setEntity(entity);
        logDTO.setEntityId(entityId);
        logDTO.setOperationType(operationType);
        logDTO.setDetails(details);
        logDTO.setNewValue(serialize(newValue));
        logDTO.setOldValue(serialize(oldValue));



    }
}
