package ca.quadrilateral.websocket.codec;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.websocket.mpstat.parser.MpStatParseResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MpStatParseResultEncoder implements Encoder.Text<MpStatParseResult> {
	private static final Logger logger = LoggerFactory.getLogger(MpStatParseResultEncoder.class);
	
    @Override
    public void init(final EndpointConfig config) {
    	logger.debug("Initializing MpStatParseResultEncoder");
    }

    @Override
    public void destroy() {
    	logger.debug("Terminating MpStatParseResultEncoder");
    }

    @Override
    public String encode(final MpStatParseResult parseResult) throws EncodeException {
        try {
            return new ObjectMapper().writeValueAsString(parseResult);
        } catch (final JsonProcessingException e) {
            throw new EncodeException(parseResult, "Error encoding parse result to JSON", e);
        }
    }
}
