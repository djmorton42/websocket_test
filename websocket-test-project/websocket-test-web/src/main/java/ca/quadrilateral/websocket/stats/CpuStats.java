package ca.quadrilateral.websocket.stats;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
        fieldVisibility=Visibility.ANY, 
        getterVisibility=Visibility.NONE, 
        isGetterVisibility=Visibility.NONE, 
        setterVisibility=Visibility.NONE)
public class CpuStats {
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
    
    public String getCpuId() {
		return cpuId;
	}

	public double getUserTime() {
		return userTime;
	}

	public double getSystemTime() {
		return systemTime;
	}

	public double getIoWaitTime() {
		return ioWaitTime;
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
