package ca.quadrilateral.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import ca.quadrilateral.websocket.mpstat.parser.MpStatParseResult;
import ca.quadrilateral.websocket.mpstat.parser.MpStatParser;

/**
 *  Uses the mpstat Linux utility to fetch CPU stats at regular intervals
 *  and emits events describing those CPU stats for any interested
 *  observers to consume
 */
@Singleton
@Startup
public class CpuUsageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CpuUsageProcessor.class);
    
    private static final String CPU_USAGE_COMMAND = "mpstat -P ON 1 1";
    
    private static final Set<String> cpuIds = new HashSet<>();
    
    @Inject
    private MpStatParser mpStatParser;
    
    @Inject
    private Event<MpStatParseResult> parseResultEvent;
    
    @Lock(LockType.READ)
    public Set<String> getCpuIds() {
    	return Collections.unmodifiableSet(new HashSet<>(cpuIds));
    }
    
    @Lock(LockType.WRITE)
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

            final MpStatParseResult mpStatParseResult = mpStatParser.parse(mpStatOutputLines);
            
            cpuIds.clear();
            cpuIds.addAll(mpStatParseResult.getCpuIds());
            
            parseResultEvent.fire(mpStatParseResult);
        } catch (final IOException e) {
            logger.warn("IOException loading CPU usage", e);
        }
    }
}
