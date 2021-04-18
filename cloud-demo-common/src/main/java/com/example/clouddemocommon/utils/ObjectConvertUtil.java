package com.example.clouddemocommon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 袁康云
 * @Title ObjectConvertUtil
 * @ProjectName jkb-plat-backend
 * @Description: 对象转换工具
 * @Date 2018/6/30 14:38
 */
@Slf4j
public final class ObjectConvertUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 反序列化 属性不匹配不抛异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 反序列化
        OBJECT_MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);
    }

    /**
     * 对象拷贝:只支持单个对象
     *
     * @param originObj      原始对象
     * @param targetObjClazz 目标对象类型
     * @param defaultVal     默认值
     * @param <T>            目标对象类型
     * @return
     */
    public static <T> T convertObject(Object originObj, Class<T> targetObjClazz, T defaultVal) {
        if (originObj != null && targetObjClazz != null) {
            try {
                final T newInstance = targetObjClazz.newInstance();
                BeanUtils.copyProperties(originObj, newInstance);
                return newInstance;
            } catch (Exception ex) {
                log.error("[对象属性拷贝异常]originObj={},targetObjClazz={},defaultVal={}", originObj, targetObjClazz, defaultVal, ex);
                return defaultVal;
            }
        }
        return defaultVal;
    }

    /**
     * 多个对象转换
     *
     * @param originObjects  原对象列表
     * @param targetObjClazz 转换的对象
     * @param defaultVal     默认返回的值
     * @param <T>
     * @return 抛异常或者原对象为空则返回默认值
     */
    public static <T> List<T> convertObjectsByJackson(List<?> originObjects, Class<T> targetObjClazz, List<T> defaultVal) {
        if (originObjects != null) {
            List<T> list = new ArrayList();
            originObjects.forEach(object -> {
                T converted = convertObjectByJackson(object, targetObjClazz, null);
                if (converted != null) {
                    list.add(converted);
                }
            });
            return list;
        }
        return defaultVal;
    }

    /**
     * 多个对象转换
     *
     * @param originObjects  原对象列表
     * @param targetObjClazz 转换的对象
     * @param defaultVal     默认返回的值
     * @param <T>
     * @return 抛异常或者原对象为空则返回默认值
     */
    public static <T> List<T> convertObjects(List<?> originObjects, Class<T> targetObjClazz, List<T> defaultVal) {
        if (originObjects != null) {
            List<T> list = new ArrayList();
            originObjects.forEach(object -> {
                list.add(convertObject(object, targetObjClazz, null));
            });
            return list;
        }
        return defaultVal;
    }

    /**
     * list对象转换为对象数组
     *
     * @param originList
     * @return
     */
    public static Object[] convertList2array(List<Object> originList) {
        return originList.toArray(new Object[originList.size()]);
    }

    /**
     * 使用jackson进行对象拷贝(可进行深度拷贝)
     * 注意:若源对象和拷贝对象为相同的类则不进行拷贝,而是直接引用
     * @param origin 源对象
     * @param targetClazz 目标对象类
     * @param defaultVal 默认值
     * @param <T>
     * @return
     */
    public static <T> T convertObjectByJackson(Object origin, Class<T> targetClazz, T defaultVal) {
        try {
            return OBJECT_MAPPER.convertValue(origin, targetClazz);
        } catch (Exception ex) {
            log.error("[对象转换异常]origin={},targetClazz={},defaultVal={}", origin, targetClazz, defaultVal, ex);
        }
        return defaultVal;
    }

    /**
     * 使用jackson序列化对象
     * @param origin 要序列化的对象
     * @param defaultVal 默认值
     * @return 抛异常返回 defaultVal
     */
    public static String writeObjectAsStringByJackson(Object origin, String defaultVal) {
        try {
            return OBJECT_MAPPER.writeValueAsString(origin);
        } catch (JsonProcessingException ex) {
            log.error("[对象序列化失败]origin={},defaultVal={}", origin, defaultVal, ex);
            return defaultVal;
        }
    }

    /**
     * 使用Jackson反序列号对象
     * @param jsonString
     * @param objClazz
     * @param defaultVal
     * @param <T>
     * @return
     */
    public static <T> T readStringAsObjectByJackson(String jsonString, Class<T> objClazz, T defaultVal) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, objClazz);
        } catch (IOException ex) {
            log.error("[对象反序列化失败]jsonString={},objClazz={},defaultVal", jsonString, objClazz, defaultVal, ex);
            return defaultVal;
        }
    }
}
