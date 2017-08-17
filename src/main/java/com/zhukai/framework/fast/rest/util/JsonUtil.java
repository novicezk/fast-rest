package com.zhukai.framework.fast.rest.util;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

public class JsonUtil {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String toJson(Object object) throws IOException {
		if (object != null && TypeUtil.isBasicType(object)) {
			return object.toString();
		}
		return objectMapper.writeValueAsString(object);
	}

	public static <T> T convertObj(String json, Class<T> clazz) throws IOException {
		if (JSONObject.class.equals(clazz)) {
			return clazz.cast(new JSONObject(json));
		}
		return objectMapper.readValue(json, clazz);
	}

	private JsonUtil() {
	}
}
