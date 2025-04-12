package org.envoyops.ctlp.xds;

import java.util.List;
import java.util.Optional;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;
import io.envoyproxy.controlplane.cache.v3.Snapshot;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment;
import io.envoyproxy.envoy.config.listener.v3.Listener;
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration;
import io.envoyproxy.envoy.extensions.access_loggers.file.v3.FileAccessLog;
import io.envoyproxy.envoy.extensions.filters.http.cors.v3.Cors;
import io.envoyproxy.envoy.extensions.filters.http.ext_authz.v3.ExtAuthz;
import io.envoyproxy.envoy.extensions.filters.http.jwt_authn.v3.JwtAuthentication;
import io.envoyproxy.envoy.extensions.filters.http.router.v3.Router;
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager;
import io.envoyproxy.envoy.extensions.transport_sockets.tls.v3.Secret;
import io.envoyproxy.envoy.extensions.upstreams.http.v3.HttpProtocolOptions;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class XdsBuilder {

	private final TypeRegistry typeRegistry;

	public XdsBuilder() {
		this.typeRegistry = TypeRegistry.newBuilder()
			.add(HttpConnectionManager.getDescriptor())
			.add(JwtAuthentication.getDescriptor())
			.add(HttpProtocolOptions.getDescriptor())
			.add(Router.getDescriptor())
			.add(Cors.getDescriptor())
			.add(ExtAuthz.getDescriptor())
			.add(FileAccessLog.getDescriptor())
			.build();
	}

	public Cluster buildCluster(String json) {
		Assert.hasLength(json, "cluster json must not be empty");
		var builder = Cluster.newBuilder();

		try {
			JsonFormat.parser().usingTypeRegistry(this.typeRegistry).merge(json, builder);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException("invalid 'Cluster' json", ex);
		}

		return builder.build();
	}

	public Listener buildListener(String json) {
		Assert.hasLength(json, "listener json must not be empty");
		var builder = Listener.newBuilder();

		try {
			JsonFormat.parser().usingTypeRegistry(this.typeRegistry).merge(json, builder);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException("invalid 'Listener' json", ex);
		}

		return builder.build();
	}

	public RouteConfiguration buildRouteConfiguration(String json) {
		Assert.hasLength(json, "RouteConfiguration json must not be empty");
		var builder = RouteConfiguration.newBuilder();

		try {
			JsonFormat.parser().usingTypeRegistry(this.typeRegistry).merge(json, builder);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException("invalid 'RouteConfiguration' json", ex);
		}

		return builder.build();
	}

	public ClusterLoadAssignment buildClusterLoadAssignment(String json) {
		Assert.hasLength(json, "Endpoint json must not be empty");
		var builder = ClusterLoadAssignment.newBuilder();
		try {
			JsonFormat.parser().usingTypeRegistry(this.typeRegistry).merge(json, builder);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException("invalid 'ClusterLoadAssignment' json", ex);
		}

		return builder.build();
	}

	public Secret buildSecret(String json) {
		Assert.hasLength(json, "Secret json must not be empty");
		var builder = Secret.newBuilder();
		try {
			JsonFormat.parser().usingTypeRegistry(this.typeRegistry).merge(json, builder);
		}
		catch (InvalidProtocolBufferException ex) {
			throw new IllegalArgumentException("invalid 'Secret' json", ex);
		}
		return builder.build();
	}

	public Snapshot buildSnapshot(NodeGroupSnapshot nodeGroupSnapshot) {
		var clusterList = Optional.ofNullable(nodeGroupSnapshot.clusters())
			.map((clusters) -> clusters.stream().map(this::buildCluster).toList())
			.orElse(List.of());

		var endpointList = Optional.ofNullable(nodeGroupSnapshot.endpoints())
			.map((endpoints) -> endpoints.stream().map(this::buildClusterLoadAssignment).toList())
			.orElse(List.of());

		var listenerList = Optional.ofNullable(nodeGroupSnapshot.listeners())
			.map((listeners) -> listeners.stream().map(this::buildListener).toList())
			.orElse(List.of());

		var routeList = Optional.ofNullable(nodeGroupSnapshot.routes())
			.map((routes) -> routes.stream().map(this::buildRouteConfiguration).toList())
			.orElse(List.of());

		var secretList = Optional.ofNullable(nodeGroupSnapshot.secrets())
			.map((secrets) -> secrets.stream().map(this::buildSecret).toList())
			.orElse(List.of());

		return Snapshot.create(clusterList, endpointList, listenerList, routeList, secretList,
				nodeGroupSnapshot.version());
	}

}
