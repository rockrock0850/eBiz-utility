package com.ebizprise.project.utility.bean.wraper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class LowerCaseKeyDeserializer extends KeyDeserializer {
    
    @Override
    public Object deserializeKey (String key, DeserializationContext ctx) throws IOException, JsonProcessingException {
        return lowerCaseFirst(key);
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