package org.envoyops.ctlp.xds;

import java.util.ArrayList;
import java.util.List;

public record NodeGroupSnapshot(String nodeGroupId, String version, List<String> clusters, List<String> endpoints,
		List<String> listeners, List<String> routes, List<String> secrets) {

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String nodeGroupId;

		private String version;

		private List<String> clusters = new ArrayList<>();

		private List<String> endpoints = new ArrayList<>();

		private List<String> listeners = new ArrayList<>();

		private List<String> routes = new ArrayList<>();

		private List<String> secrets = new ArrayList<>();

		public Builder nodeGroupId(String nodeGroupId) {
			this.nodeGroupId = nodeGroupId;
			return this;
		}

		public Builder version(String version) {
			this.version = version;
			return this;
		}

		public Builder clusters(List<String> clusters) {
			this.clusters = new ArrayList<>(clusters);
			return this;
		}

		public Builder addCluster(String cluster) {
			this.clusters.add(cluster);
			return this;
		}

		public Builder endpoints(List<String> endpoints) {
			this.endpoints = new ArrayList<>(endpoints);
			return this;
		}

		public Builder addEndpoint(String endpoint) {
			this.endpoints.add(endpoint);
			return this;
		}

		public Builder listeners(List<String> listeners) {
			this.listeners = new ArrayList<>(listeners);
			return this;
		}

		public Builder addListener(String listener) {
			this.listeners.add(listener);
			return this;
		}

		public Builder routes(List<String> routes) {
			this.routes = new ArrayList<>(routes);
			return this;
		}

		public Builder addRoute(String route) {
			this.routes.add(route);
			return this;
		}

		public Builder secrets(List<String> secrets) {
			this.secrets = new ArrayList<>(secrets);
			return this;
		}

		public Builder addSecret(String secret) {
			this.secrets.add(secret);
			return this;
		}

		public NodeGroupSnapshot build() {
			return new NodeGroupSnapshot(this.nodeGroupId, this.version, List.copyOf(this.clusters),
					List.copyOf(this.endpoints), List.copyOf(this.listeners), List.copyOf(this.routes),
					List.copyOf(this.secrets));
		}

	}

}
