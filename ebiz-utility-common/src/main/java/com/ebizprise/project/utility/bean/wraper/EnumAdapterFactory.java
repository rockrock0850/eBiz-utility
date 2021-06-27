package com.ebizprise.project.utility.bean.wraper;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class EnumAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create (final Gson gson, final TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        
        if (rawType.isEnum()) {
            return new EnumTypeAdapter<T>();
        }
        
        return null;
    }

    public class EnumTypeAdapter<T> extends TypeAdapter<T> {
        
        @Override
        public void write (JsonWriter out, T value) throws IOException {
            if (value == null || !value.getClass().isEnum()) {
                out.nullValue();
                return;
            }

            try {
                out.beginObject();
                out.name("value");
                out.value(value.toString());
                
                PropertyDescriptor[] descriptors = Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors();
                for (PropertyDescriptor descriptor : descriptors) {
                    if (isReadMethod(descriptor)) {
                        try {
                            out.name(descriptor.getName());
                            out.value(String.valueOf(descriptor.getReadMethod().invoke(value)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                out.endObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public T read (JsonReader in) throws IOException {
            return null;
        }

        private boolean isReadMethod (PropertyDescriptor descriptor) {
            return descriptor.getReadMethod() != null && 
                    !"class".equals(descriptor.getName()) && 
                    !"declaringClass".equals(descriptor.getName());
        }
    }
    
}