package ca.quadrilateral.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import ca.quadrilateral.websocket.MpStatParseResult.CpuStats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CpuStatsEncoder implements Encoder.Text<CpuStats> {

    @Override
    public void init(final EndpointConfig config) {
        
    }

    @Override
    public void destroy() {
        
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
