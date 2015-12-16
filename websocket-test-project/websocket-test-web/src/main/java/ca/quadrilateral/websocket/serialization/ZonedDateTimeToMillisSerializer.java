package ca.quadrilateral.websocket.serialization;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeToMillisSerializer extends JsonSerializer<ZonedDateTime> {

    @Override
    public void serialize(
            final ZonedDateTime value, 
            final JsonGenerator jsonGenerator,
            final SerializerProvider provider) throws IOException, JsonProcessingException {
        
        jsonGenerator.writeNumber(value.toInstant().toEpochMilli());
    }

}
