package ca.quadrilateral.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ca.quadrilateral.websocket.CpuUsageProcessor;

@Path("cores")
public class CoreResource {
	@Inject
	private CpuUsageProcessor cpuUsageProcessor;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoreList() {
		final List<String> cpuIds = new ArrayList<String>(cpuUsageProcessor.getCpuIds());

		Collections.sort(cpuIds, cpuListComparator);
		
		return Response.ok(cpuIds).build();
	}
		
	private static final Comparator<String> cpuListComparator = new Comparator<String>() {
		
		@Override
		public int compare(final String firstCpuId, final String secondCpuId) {
			if ("all".equalsIgnoreCase(firstCpuId)) {
				return -1;
			} else if ("all".equalsIgnoreCase(secondCpuId)) {
				return 1;
			} else {
				return Integer.compare(Integer.parseInt(firstCpuId), Integer.parseInt(secondCpuId));
			}
		}
		
	};
}
