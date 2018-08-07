package com.ftvalue.blockchain.countdown.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * Created on 2016/11/30.
 *
 * @author Ji Zhou
 */
public class JsonUtil {
    public static final ObjectMapper MAPPER = initObjectMapper();

    public static <T> T toObject(String content, Class<T> valueType) {
        try {
            return MAPPER.readValue(content, valueType);
        } catch (IOException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    public static <T> T toObject(String content, TypeReference valueTypeRef) {
        try {
            return MAPPER.readValue(content, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    public static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    public static byte[] toJsonBytes(Object o) {
        try {
            return MAPPER.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON Bytes失败", e);
        }
    }

    /**
     * 把对象生成为key值没有双引号的JSON
     *
     * @param o
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnquotedJson(Object o) {
        MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        String nonStandardJson = null;
        try {
            nonStandardJson = MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转Unquoted JSON失败", e);
        }
        MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        return nonStandardJson;
    }

    /**
     * 把JSON object里name/value中name值没有双引号的非标准JSON转为对象
     * 如把{aName:"value"}转为对Java对象
     *
     * @param content
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T allowUnquotedFieldJsonToObject(String content, Class<T> valueType) {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        T result;
        try {
            result = MAPPER.readValue(content, valueType);
        } catch (IOException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
        return result;
    }

    public static boolean isValid(String json) {
        if (null == json) return false;
        boolean valid = true;
        try {
            MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            valid = false;
        } catch (IOException e) {
            throw new RuntimeException("检查JSON是否合法时发生IO异常", e);
        }
        return valid;
    }

    public static ObjectMapper initObjectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }



    public static boolean isGoodJson(String content, Class valueType) {
        try {
            Object object = MAPPER.readValue(content, valueType);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
