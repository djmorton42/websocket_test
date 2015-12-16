package ca.quadrilateral.websocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.websocket.codec.CpuStatsEncoder;
import ca.quadrilateral.websocket.codec.MpStatParseResultEncoder;
import ca.quadrilateral.websocket.mpstat.parser.MpStatParseResult;

@ServerEndpoint(
        value = "/cpu/{cores}",
        encoders = 
            {
                MpStatParseResultEncoder.class,
                CpuStatsEncoder.class
            }
        )
public class CpuUsageWebsocket {
    private static final Logger logger = LoggerFactory.getLogger(CpuUsageWebsocket.class);
    
    @Inject
    private SessionStore sessionStore;
            
    @OnOpen
    public void openConnection(final Session session, @PathParam("cores") final String cores) {
        logger.info("Connection opened: {}", session);
        final Set<String> coreSet = parseCores(cores);
        
        sessionStore.addSession(session, coreSet);
    }
    
    @OnClose
    public void closeConnection(final Session session, @PathParam("cores") final String cores) {
        logger.info("Connection closed: {}", session);
        sessionStore.removeSession(session);
    }
    
    @OnMessage
    public void onMessage(final Session session, @PathParam("cores") final String cores, final String message) {
        
    }
    
    private Set<String> parseCores(final String coresString) {
    	final String[] coresArray = StringUtils.split(coresString, ",");
    	return new HashSet<>(Arrays.asList(coresArray));
    }
    
    public void onIncomingMpStatParseResult(@Observes final MpStatParseResult parseResult) {
        logger.debug("Received parse result event");
        sessionStore
                .getSessions()
                .stream()
                .forEach(sessionContainer -> sendMpStatsToBrowser(sessionContainer, parseResult));	
    }

	private void sendMpStatsToBrowser(final SessionContainer sessionContainer, final MpStatParseResult mpStatParseResult) {
	    try {
	    	final Session session = sessionContainer.getSession();
	    	final Set<String> cores = sessionContainer.getCores();
	    	
	        session.getBasicRemote().sendObject(mpStatParseResult.filter(cores));
	    } catch (final EncodeException | IOException e) {
	        logger.error("Error encoding MpStat parse results", e);
	    }
	}

}
