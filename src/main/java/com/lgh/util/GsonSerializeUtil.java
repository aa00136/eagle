package com.lgh.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonSerializeUtil {
	private final static Gson GSON = new Gson();

	public static JsonObject toJsonObject(Object obj) {
		if (obj != null) {
			return GSON.toJsonTree(obj).getAsJsonObject();
		} else {
			return new JsonObject();
		}
	}

	public static String toJson(Object obj) {
		if (obj != null) {
			return GSON.toJson(obj);
		} else {
			return null;
		}
	}

	public static <T> JsonArray toJsonArray(List<T> objs, Class<T> clazz) {
		if (objs != null) {
			return GSON.toJsonTree(objs, new TypeToken<List<T>>() {
			}.getType()).getAsJsonArray();
		} else {
			return new JsonArray();
		}
	}

	public static <T> T fromJson(JsonObject jsonObject, Class<T> clazz) {
		if (jsonObject != null) {
			return GSON.fromJson(jsonObject, clazz);
		} else {
			return null;
		}
	}

	public static <T> List<T> fromJsonArray(JsonArray jsonArray, Class<T> clazz) {
		if (jsonArray != null && jsonArray.size() > 0) {
			return GSON.fromJson(jsonArray, new TypeToken<List<T>>() {
			}.getType());
		} else {
			return new ArrayList<T>();
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		if (StringUtils.isNotBlank(json)) {
			return GSON.fromJson(json, clazz);
		} else {
			return null;
		}
	}

	public static Map<String, Object> fromJson(String json) {
		if (StringUtils.isNotBlank(json)) {
			return GSON.fromJson(json, new TypeToken<HashMap<String,Object>>() {
			}.getType());
		} else {
			return null;
		}
	}
}
