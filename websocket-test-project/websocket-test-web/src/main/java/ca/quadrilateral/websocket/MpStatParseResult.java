package ca.quadrilateral.websocket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
    
    @JsonAutoDetect(
            fieldVisibility=Visibility.ANY, 
            getterVisibility=Visibility.NONE, 
            isGetterVisibility=Visibility.NONE, 
            setterVisibility=Visibility.NONE)
    public static class CpuStats {
        private final String cpuId;
        private final double userTime;
        private final double systemTime;
        private final double ioWaitTime;
        
        private CpuStats(
                final String cpuId, 
                final double userTime, 
                final double systemTime, 
                final double ioWaitTime) {
            
            this.cpuId = cpuId;
            this.userTime = userTime;
            this.systemTime = systemTime;
            this.ioWaitTime = ioWaitTime;
        }
        
        public static CpuStats of(
                final String cpuId, 
                final double userTime, 
                final double systemTime, 
                final double ioWaitTime) {
            
            return new CpuStats(cpuId, userTime, systemTime, ioWaitTime);
        }
        
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                .append("CpuID", cpuId)
                .append("usr", userTime)
                .append("sys", systemTime)
                .append("ioWait", ioWaitTime)
                .toString();
        }
    }
}
