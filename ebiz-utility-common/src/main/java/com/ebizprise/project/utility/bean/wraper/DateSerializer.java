package com.ebizprise.project.utility.bean.wraper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DateSerializer extends StdSerializer<Date> {
    
    private static final long serialVersionUID = 1L;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public DateSerializer() {
        this(null);
    }

    public DateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize (Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateFormat.format(date));
    }
    
}
