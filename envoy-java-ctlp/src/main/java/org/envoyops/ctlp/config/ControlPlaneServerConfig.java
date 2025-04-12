package org.envoyops.ctlp.config;

import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.envoyproxy.controlplane.cache.v3.SimpleCache;
import io.envoyproxy.controlplane.server.V3DiscoveryServer;
import io.envoyproxy.envoy.config.core.v3.Node;
import org.envoyops.ctlp.service.ControlPlaneService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(ControlPlaneProperties.class)
@Configuration
public class ControlPlaneServerConfig {

	@Bean
	public SimpleCache<String> snapshotCache() {
		return new SimpleCache<>(Node::getId);
	}

	@Bean
	public V3DiscoveryServer discoveryServer(SimpleCache<String> snapshotCache) {
		return new V3DiscoveryServer(snapshotCache);
	}

	@Bean
	public ArmeriaServerConfigurator serverConfigurator(V3DiscoveryServer discoveryServer,
			ControlPlaneService controlPlaneService) {
		return (serverBuilder) -> {
			var grpcService = GrpcService.builder()
				.addService(discoveryServer.getAggregatedDiscoveryServiceImpl())
				.addService(discoveryServer.getEndpointDiscoveryServiceImpl())
				.addService(discoveryServer.getListenerDiscoveryServiceImpl())
				.addService(discoveryServer.getRouteDiscoveryServiceImpl())
				.addService(discoveryServer.getClusterDiscoveryServiceImpl())
				.enableHealthCheckService(true)
				.enableUnframedRequests(true)
				.build();
			serverBuilder.service(grpcService, LoggingService.newDecorator());
			serverBuilder.annotatedService("/ctlp", controlPlaneService);
			serverBuilder.serviceUnder("/docs", new DocService());
		};
	}

}
