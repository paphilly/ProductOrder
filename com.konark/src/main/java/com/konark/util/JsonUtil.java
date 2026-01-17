package com.konark.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.enable(SerializationFeature.INDENT_OUTPUT);

	// Private constructor to prevent instantiation
	private JsonUtil() {
	}

	/**
	 * Convert JSON string to Java object
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert JSON to object", e);
		}
	}

	/**
	 * Convert Java object to JSON string
	 */
	public static String toJson(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to convert object to JSON", e);
		}
	}
}
