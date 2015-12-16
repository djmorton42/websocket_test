package ca.quadrilateral.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;

public class SessionContainer {
	private final Session session;
	private final Set<String> cores;
	
	SessionContainer(final Session session, final Set<String> cores) {
		this.session = session;
		this.cores = cores;
	}
	
	public static final SessionContainer of(final Session session, final Set<String> cores) {
		return new SessionContainer(session, cores);
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public Set<String> getCores() {
		return Collections.unmodifiableSet(new HashSet<>(cores));
	}
	
	public void setCores(final Set<String> cores) {
		synchronized(this) {
			this.cores.clear();
			this.cores.addAll(cores);
		}
	}
}
