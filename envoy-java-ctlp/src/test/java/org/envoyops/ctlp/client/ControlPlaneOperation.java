package org.envoyops.ctlp.client;

import org.envoyops.ctlp.xds.NodeGroupSnapshot;

import org.springframework.web.client.RestClient;

public class ControlPlaneOperation extends RestOperation {

	public ControlPlaneOperation(RestClient restClient) {
		super(restClient);
	}

	public void publishSnapshot(NodeGroupSnapshot nodeGroupSnapshot) {
		this.doPut("/ctlp/snapshot", null, null, nodeGroupSnapshot, String.class);

	}

}
