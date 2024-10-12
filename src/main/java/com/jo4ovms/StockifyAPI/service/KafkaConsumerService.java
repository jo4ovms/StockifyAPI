package com.jo4ovms.StockifyAPI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.model.Log;
import com.jo4ovms.StockifyAPI.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.jo4ovms.StockifyAPI.model.Log.OperationType;

@Service
public class KafkaConsumerService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "logs", groupId = "logging-group")
    public void consume(String message) {
        try {

            LogDTO logDTO = objectMapper.readValue(message, LogDTO.class);

            Log log = new Log();
            log.setTimestamp(logDTO.getTimestamp());
            log.setEntity(logDTO.getEntity());
            log.setEntityId(logDTO.getEntityId());
            log.setOperationType(OperationType.valueOf(logDTO.getOperationType()));
            log.setOldValue(logDTO.getOldValue());
            log.setNewValue(logDTO.getNewValue());
            log.setDetails(logDTO.getDetails());
            logRepository.save(log);

            System.out.println("Log saved: " + log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
