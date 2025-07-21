package com.shiwu.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSON工具类，用于对象和JSON字符串之间的转换
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // 添加Java 8时间模块支持
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 将对象转换为JSON字符串
     * @param obj 要转换的对象
     * @return JSON字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象转JSON失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     * @throws RuntimeException 如果转换失败
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            throw new IllegalArgumentException("JSON字符串不能为null");
        }
        if (json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON字符串不能为空");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("目标类型不能为null");
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("JSON转对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("JSON转对象失败: " + e.getMessage(), e);
        }
    }
}