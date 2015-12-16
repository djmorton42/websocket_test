package ca.quadrilateral.websocket.mpstat.parser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.quadrilateral.websocket.serialization.ZonedDateTimeToMillisSerializer;
import ca.quadrilateral.websocket.stats.CpuStats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(
        fieldVisibility=Visibility.ANY, 
        getterVisibility=Visibility.NONE, 
        isGetterVisibility=Visibility.NONE, 
        setterVisibility=Visibility.NONE)
public class MpStatParseResult {
    @JsonSerialize(using = ZonedDateTimeToMillisSerializer.class)
    private final ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
    
    @JsonIgnore
    private Map<String, CpuStats> cpuStatsMap = new HashMap<>();
    
    @JsonProperty("cpuStats")
    private Set<CpuStats> cpuStatsSet = new HashSet<>();
    
    public MpStatParseResult() {}
    
    public MpStatParseResult addCpuStats(
            final String cpuId, 
            final double userTime, 
            final double systemTime, 
            final double ioWaitTime) {
        
        final CpuStats cpuStats = CpuStats.of(cpuId, userTime, systemTime, ioWaitTime);
        cpuStatsMap.put(cpuId, cpuStats);
        cpuStatsSet.add(cpuStats);
        
        return this;
    }
    
    public MpStatParseResult addCpuStats(final CpuStats cpuStats) {
    	this.addCpuStats(cpuStats.getCpuId(), cpuStats.getUserTime(), cpuStats.getSystemTime(), cpuStats.getIoWaitTime());
    	return this;
    }
    
    public MpStatParseResult filter(final Set<String> cores) {
    	final MpStatParseResult filteredResult = new MpStatParseResult();
    	for (final CpuStats cpuStats : cpuStatsSet) {
    		if (cores.contains(cpuStats.getCpuId())) {
    			filteredResult.addCpuStats(cpuStats);
    		}
    	}
    	
    	return filteredResult;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    
    public Set<String> getCpuIds() {
        return Collections.unmodifiableSet(new HashSet<>(cpuStatsMap.keySet()));
    }   
    
    public CpuStats getCpuStatsForId(final String cpuId) {
        return cpuStatsMap.get(cpuId);
    }
    
    public Collection<CpuStats> getCpuStats() {
        return Collections.unmodifiableCollection(new HashSet<>(cpuStatsMap.values()));
    }
    
    public static class EmptyMpStatParseResult extends MpStatParseResult {
        @Override
        public MpStatParseResult addCpuStats(
                final String cpuId,
                final double userTime,
                final double systemTime,
                final double ioWaitTime) {
            throw new UnsupportedOperationException("This empty result is immutable!");
        }
    }
}
