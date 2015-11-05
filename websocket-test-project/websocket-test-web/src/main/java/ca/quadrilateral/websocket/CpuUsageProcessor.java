package ca.quadrilateral.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@Lock(LockType.READ)
public class CpuUsageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CpuUsageProcessor.class);
    
    private static final String CPU_USAGE_COMMAND = "mpstat -P ON 1 1";
    
    @Inject
    private MpStatParser mpStatParser;
    
    @Inject
    private Event<MpStatParseResult> parseResultEvent;
    
    @Timeout @Schedule(persistent = false, second="*/10", minute="*", hour="*")
    public void fetchCpuUsage() {
        final List<String> mpStatOutputLines = new ArrayList<>();
        try {
            final Process process = Runtime.getRuntime().exec(CPU_USAGE_COMMAND);
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String inLine = null;
                while ((inLine = reader.readLine()) != null) {
                    mpStatOutputLines.add(inLine);
                }
            }
        } catch (final IOException e) {
            logger.warn("IOException loading CPU usage", e);
        }
        
        final MpStatParseResult mpStatParseResult = mpStatParser.parse(mpStatOutputLines);
      
        parseResultEvent.fire(mpStatParseResult);
    }
    
}
