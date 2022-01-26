package com.itech.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * JsonEntitySerializer class. Provides us methods for mapping JSON string from object and obtain
 * object from JSON string.
 *
 * @author Edvard Krainiy on 12/30/2021
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class JsonEntitySerializer {
  private final ObjectMapper objectMapper;

  /**
   * serializeObjectToJson method. Converts object of any class to JSON string and returns this one.
   *
   * @param object Object of class we need to convert to JSON string.
   * @param <T> Generic class of object.
   * @return String Obtained JSON String.
   */
  public <T> String serializeObjectToJson(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException exception) {
      log.error("JsonProcessingException caught!");
      return "";
    }
  }

  /**
   * serializeJsonToObject method. Converts JSON string to Object and returns this one.
   *
   * @param json JSON String we want to convert to object.
   * @param tClass Class of object we want to obtain.
   * @param <T> Generic class of object.
   * @return T Obtained object.
   */
  public <T> T serializeJsonToObject(String json, Class<T> tClass) {
    try {
      return objectMapper.readValue(json, tClass);
    } catch (JsonProcessingException exception) {
      log.error("JsonProcessingException caught!");
      return null;
    }
  }
}
