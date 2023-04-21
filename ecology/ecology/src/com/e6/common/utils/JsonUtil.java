package com.e6.common.utils;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

/**
 * json二度封装工具类
 * 
 * @author HM
 *
 */
public class JsonUtil {

	private final static Logger log = Logger.getLogger(JsonUtil.class);

	/**
	 * 转换json字符串
	 * <p>
	 * 
	 * @Title:toString
	 *                 </p>
	 *                 <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param o
	 * @return
	 */
	public static String toString(Object o) {
		return new GsonBuilder().create().toJson(o);
	}

	/**
	 * json转换实体对象
	 * <p>
	 * 
	 * @Title:toObject
	 *                 </p>
	 *                 <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		return new GsonBuilder().create().fromJson(json, clazz);
	}

	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * 对象转换成json字符串
	 * <p>
	 * 
	 * @Title:toJson
	 *               </p>
	 *               <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error(e.toString(), e);
		}
		return "{}";
	}

	public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
		return mapper.readValue(json, clazz);
	}
}
