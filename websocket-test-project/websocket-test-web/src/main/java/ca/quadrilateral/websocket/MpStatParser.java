package ca.quadrilateral.websocket;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import ca.quadrilateral.websocket.MpStatParseResult.EmptyMpStatParseResult;

@ApplicationScoped
public class MpStatParser {
    public MpStatParseResult parse(final List<String> mpStatOutputLines) {
        final int outputLineCount = mpStatOutputLines.size();
        
        if (outputLineCount > 3) {
            final MpStatParseResult mpStatParseResult = new MpStatParseResult();
            final String keyLine = mpStatOutputLines.get(2);
            final List<String> cpuLines = mpStatOutputLines.subList(3, mpStatOutputLines.size());
            
            final int cpuIndex = keyLine.indexOf("CPU");
            if (cpuIndex == -1) {
                return new EmptyMpStatParseResult();
            } else {
                cpuLines
                    .stream()
                    .filter(x -> !x.startsWith("Average") && !x.isEmpty())
                    .map(x -> x.substring(cpuIndex))
                    .map(x -> x.replaceAll("  ", " "))
                    .map(x -> x.trim())
                    .map(x -> x.split(" "))
                    .forEach(x -> mpStatParseResult.addCpuStats(
                            x[0], 
                            Double.parseDouble(x[2]), 
                            Double.parseDouble(x[6]), 
                            Double.parseDouble(x[8])));
            }            
            
            return mpStatParseResult;
        } else {
            return new EmptyMpStatParseResult();
        }
    }
}
