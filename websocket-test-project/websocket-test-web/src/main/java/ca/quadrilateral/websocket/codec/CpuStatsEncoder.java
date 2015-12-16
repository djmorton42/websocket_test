package ca.quadrilateral.websocket.codec;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.websocket.stats.CpuStats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CpuStatsEncoder implements Encoder.Text<CpuStats> {
	private static final Logger logger = LoggerFactory.getLogger(CpuStatsEncoder.class);
		
    @Override
    public void init(final EndpointConfig config) {
    	logger.debug("Initializing CpuStatsEncoder");
    }

    @Override
    public void destroy() {
    	logger.debug("Terminating CpuStatsEncoder");
    }

    @Override
    public String encode(final CpuStats cpuStats) throws EncodeException {
        try {
            return new ObjectMapper().writeValueAsString(cpuStats);
        } catch (final JsonProcessingException e) {
            throw new EncodeException(cpuStats, "Error encoding CpuStats object", e);
        }
    }

}
