package ca.quadrilateral.websocket.mpstat.parser;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import ca.quadrilateral.websocket.mpstat.parser.MpStatParseResult.EmptyMpStatParseResult;

/**
 * Given a list of lines containing output from the mpstat command
 * this class will parse that output, extracting the relevant CPU
 * usage data.
 * 
 * Example Output:
 * 
 * <pre>
 * Linux 3.19.8-100.fc20.x86_64 (andromache) 	12/10/2015 	_x86_64_	(4 CPU)
 * 
 * 10:43:40 PM  CPU    %usr   %nice    %sys %iowait    %irq   %soft  %steal  %guest  %gnice   %idle
 * 10:43:41 PM  all    0.50    0.00    0.25    0.00    0.00    0.00    0.00    0.00    0.00   99.25
 * 10:43:41 PM    0    1.98    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00   98.02
 * 10:43:41 PM    1    0.00    0.00    1.00    0.00    0.00    0.00    0.00    0.00    0.00   99.00
 * 10:43:41 PM    2    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00  100.00
 * 10:43:41 PM    3    1.98    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00   98.02
 * 
 * Average:     CPU    %usr   %nice    %sys %iowait    %irq   %soft  %steal  %guest  %gnice   %idle
 * Average:     all    0.50    0.00    0.25    0.00    0.00    0.00    0.00    0.00    0.00   99.25
 * Average:       0    1.98    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00   98.02
 * Average:       1    0.00    0.00    1.00    0.00    0.00    0.00    0.00    0.00    0.00   99.00
 * Average:       2    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00  100.00
 * Average:       3    1.98    0.00    0.00    0.00    0.00    0.00    0.00    0.00    0.00   98.02
 * </pre>
 */
@ApplicationScoped
public class MpStatParser {
    private static final int CPU_NAME_COLUMN_INDEX = 0;
    private static final int USER_TIME_COLUMN_INDEX = 2;
    private static final int SYSTEM_TIME_COLUMN_INDEX = 6;
    private static final int IO_WAIT_TIME_COLUMN_INDEX = 8;
	
    private static final int TITLE_LINE_NUMBER = 2;
    
    public MpStatParseResult parse(final List<String> mpStatOutputLines) {
        final int outputLineCount = mpStatOutputLines.size();
        
        if (outputLineCount > 3) {
            final MpStatParseResult mpStatParseResult = new MpStatParseResult();
            final String keyLine = mpStatOutputLines.get(TITLE_LINE_NUMBER);
            final List<String> cpuLines = mpStatOutputLines.subList(TITLE_LINE_NUMBER + 1, mpStatOutputLines.size());
            
            final int cpuIndex = keyLine.indexOf("CPU");
            if (cpuIndex == -1) {
                return new EmptyMpStatParseResult();
            } else {
                cpuLines
                    .stream()
                    .filter(line -> !line.startsWith("Average") && !line.isEmpty())
                    .map(line -> line.substring(cpuIndex))
                    .map(line -> line.replaceAll("  ", " "))
                    .map(line -> line.trim())
                    .map(line -> line.split(" "))
                    .forEach(tokenArray -> mpStatParseResult.addCpuStats(
                            tokenArray[CPU_NAME_COLUMN_INDEX], 
                            Double.parseDouble(tokenArray[USER_TIME_COLUMN_INDEX]), 
                            Double.parseDouble(tokenArray[SYSTEM_TIME_COLUMN_INDEX]), 
                            Double.parseDouble(tokenArray[IO_WAIT_TIME_COLUMN_INDEX])));
            }            
            
            return mpStatParseResult;
        } else {
            return new EmptyMpStatParseResult();
        }
    }    
}
