package com.itech.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Edvard Krainiy on 12/30/2021
 */
@Component
@Log4j2
public class JsonEntitySerializer {
    @Autowired
    private ObjectMapper objectMapper;

    public <T> String serializeObjectToJson(T object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException exception){
            log.error("JsonProcessingException caught!");
            return "";
        }
    }
}
