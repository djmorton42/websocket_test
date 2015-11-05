package ca.quadrilateral.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MpStatParseResultEncoder implements Encoder.Text<MpStatParseResult> {

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(final MpStatParseResult parseResult) throws EncodeException {
        try {
            return new ObjectMapper().writeValueAsString(parseResult);
            //return new ObjectMapper().writeValueAsString(parseResult.toMutableMpStatParseResult());
        } catch (final JsonProcessingException e) {
            throw new EncodeException(parseResult, "Error encoding parse result to JSON", e);
        }
    }
}
