package ca.quadrilateral.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

@ApplicationScoped
public class SessionStore {
    private final Map<String, Session> idSessionMap = new ConcurrentHashMap<>();
    
    public void addSession(final Session session) {
        idSessionMap.put(session.getId(), session);
    }
    
    public Collection<Session> getSessions() {
        return Collections.unmodifiableCollection(new ArrayList<>(idSessionMap.values()));
    }
    
    public void removeSession(final Session session) {
        idSessionMap.remove(session.getId());
    }
}
