package ca.quadrilateral.websocket;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    public void onIncomingMpStatParseResult(@Observes final MpStatParseResult parseResult) {
            logger.info("Got parse result event");
            sessionStore
                    .getSessions()
                    .stream()
                    .forEach(s -> 
                    {
                        try {
                            s.getBasicRemote().sendObject(parseResult);
                        } catch (final EncodeException | IOException e) {
                            logger.error("Error encoding MpStat parse results", e);
                        }
                    });
    }
    
    @OnOpen
    public void openConnection(final Session session, @PathParam("cores") final String cores) {
        logger.info("Connection opened: {}", session);
        sessionStore.addSession(session);
    }
    
    @OnClose
    public void closeConnection(final Session session, @PathParam("cores") final String cores) {
        logger.info("Connection closed: {}", session);
        sessionStore.removeSession(session);
    }
    
    @OnMessage
    public void onMessage(final Session session, @PathParam("cores") final String cores, final String message) {
        
    }
}
