package com.ebizprise.project.utility.bean;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.ebizprise.project.utility.bean.wraper.DateSerializer;
import com.ebizprise.project.utility.bean.wraper.EnumAdapterFactory;
import com.ebizprise.project.utility.bean.wraper.LowerCaseKeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JavaBean操作類別
 * 
 * @author adam.yeh
 *
 */
public abstract class BeanUtil extends BeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    
    private static GsonBuilder builder;
    private static ObjectMapper mapper;
    private static SimpleModule module;
    
    static {
        JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize (Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };

        FieldNamingStrategy customPolicy = new FieldNamingStrategy() {  
            @Override
            public String translateName(Field f) {
                return lowerCaseFirst(f.getName());
            }
        };
          
        builder = new GsonBuilder()
                .setFieldNamingStrategy(customPolicy)
                .registerTypeAdapter(Date.class, serializer)
                .registerTypeAdapter(Date.class, deserializer)
                .registerTypeAdapterFactory(new EnumAdapterFactory())
                .serializeNulls();

        module = new SimpleModule();
        module.addSerializer(Date.class, new DateSerializer());
        module.addKeyDeserializer(Object.class, new LowerCaseKeyDeserializer());
        
        mapper = new ObjectMapper();
        mapper.registerModule(module);
    }; 
    
    /**
     * 跳脫JSON專屬的特殊字元
     * @param json
     * @return
     */
    public static String jEscape (String json) {
        if (StringUtils.isNotBlank(json)) {
            json = JSONObject.escape(json);
            json = json.replace("'", "\\'");
        }
        
        return json;
    }
    
    /**
     * Java bean to Map
     * 
     * @param o
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> toMap (Object o) {
        return mapper.convertValue(o, Map.class);
    }
    
    /**
     * 複製串列<br>
     * P.S. 不支援巢狀物件
     * 
     * @param  froms 來源串列
     * @param  clazz 目標串列內容物件
     * @return List 目標物件
     */
    @SuppressWarnings("unchecked")
    public static <T>List<T> copyList (List<?> froms, Class<T> clazz) {
        if (froms == null || froms.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<T> tos = new ArrayList<>();

        try {
            for (Object from : froms) {
                Object to;
                to = clazz.newInstance();
                copyProperties(from, to);
                tos.add((T) to);
            }
        } catch (Exception e) {
            logger.error("The List is not correct.", e);
        }
        
        return tos;
    }

    /**
     * 將傳入物件轉成JSON字串
     * 
     * @param src
     * @return String
     */
    public static String toJson (Object src) {
        return toJson(src, builder);
    }

    /**
     * 將傳入物件轉成JSON字串
     * 
     * @param src
     * @param builder
     * @return
     */
    public static String toJson (Object src, GsonBuilder builder) {
        return builder.create().toJson(src);
    }

    /**
     * 將JSON字串轉成Map
     * 
     * @param json
     * @return String
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> fromJson (String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 將JSON字串轉成物件
     * 
     * @param json
     * @param clazz
     * @return <T>
     */
    public static <T> T fromJson (String json, Class<T> clazz) {
        return fromJson(json, builder, clazz);
    }

    /**
     * 將JSON字串轉成物件
     * 
     * @param json
     * @param builder
     * @param clazz
     * @return
     */
    public static <T> T fromJson (String json, GsonBuilder builder, Class<T> clazz) {
        return builder.create().fromJson(json, clazz);
    }

    /**
     * 將JSON字串轉成 List of VO.
     * 
     * @param json
     * @param clazz
     * @return List<T>
     */
    public static <T> List<T> fromJsonToList (String json, Class<?> clazz) {
        return builder.create().fromJson(json, new ParameterTypeWrapper<>(clazz));
    }

    /**
     * 將JSON字串轉成 List of VO.
     * 
     * @param json
     * @param builder
     * @param clazz
     * @return
     */
    public static <T> List<T> fromJsonToList (String json, GsonBuilder builder, Class<?> clazz) {
        return builder.create().fromJson(json, new ParameterTypeWrapper<>(clazz));
    }
    
    /**
     * 兼容軟/硬拷貝
     * @param source
     * @param target
     * @param isHard 是不是要連NULL都直接進行覆蓋
     * @throws BeansException
     */
    public static void copyProperties (Object source, Object target, boolean isHard) throws BeansException {
        copyProperties(source, target, null, isHard, (String[]) null);
    }
    
    /**
     * 
     * @param source
     * @param target
     * @throws BeansException
     */
    public static void copyProperties (Object source, Object target) throws BeansException {
        copyProperties(source, target, null, false, (String[]) null);
    }

    /**
     * 
     * @param source
     * @param target
     * @param editable
     * @throws BeansException
     */
    public static void copyProperties (Object source, Object target, Class<?> editable) throws BeansException {
        copyProperties(source, target, editable, false, (String[]) null);
    }

    /**
     * 
     * @param source
     * @param target
     * @param isHard
     * @param ignoreProperties
     * @throws BeansException
     * @author adam.yeh
     */
    public static void copyProperties (Object source, Object target, boolean isHard, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, isHard, ignoreProperties);
    }

    /**
     * 
     * @param source
     * @param target
     * @param ignoreProperties
     * @throws BeansException
     */
    public static void copyProperties (Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, false, ignoreProperties);
    }

    /**
     * 
     * @param source
     * @param target
     * @param editable
     * @param ignoreProperties
     * @throws BeansException
     */
    private static void copyProperties (Object source, Object target, Class<?> editable, boolean isHard, String... ignoreProperties)
            throws BeansException {

        if (source == null) {
            return;
        }
        
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            
                            Object value = readMethod.invoke(source);
                            
                            if (value == null && !isHard) {
                                continue;
                            }
                            
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            
                            writeMethod.invoke(target, value);
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * 參數化型別 封裝物件
     * 
     */
    private static class ParameterTypeWrapper<T> implements ParameterizedType {

        private Class<?> wrapped;

        ParameterTypeWrapper (Class<?> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Type[] getActualTypeArguments () {
            return new Type[] { wrapped };
        }

        @Override
        public Type getRawType () {
            return List.class;
        }

        @Override
        public Type getOwnerType () {
            return null;
        }

    }
    
    private static String lowerCaseFirst (String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        
        char[] array = value.toCharArray();
        array[0] = Character.toLowerCase(array[0]);

        return new String(array);
    }
    
}