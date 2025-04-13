package org.envoyops.ctlp.test;

import org.envoyops.ctlp.xds.XdsBuilder;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
public class ControlPlaneDevTests extends AbstractControlPlaneTests {

	@Test
	void checkProperties() {
		assertThat(this.testProperties.getEnvoyControlPlaneUrl()).isEqualTo("http://192.168.0.14:30010");
	}

	@Test
	void publishSnapshot() {
		var version = "1.0.2";
		var snapshot = parseNodeGroupSnapshot("test-envoy", version);
		assertThat(snapshot).isNotNull();
		assertThat(snapshot.clusters()).isNotEmpty();
		assertThat(snapshot.listeners()).isNotEmpty();
		assertThat(snapshot.routes()).isNotEmpty();
		assertThat(snapshot.version()).isEqualTo(version);
		assertThat(snapshot.nodeGroupId()).isEqualTo("test-envoy");
		this.controlPlaneOperation.publishSnapshot(snapshot);
	}

	@Test
	void buildListener() {
		var snapshot = parseNodeGroupSnapshot("test-envoy", "1.0.0");
		var json = snapshot.listeners().getFirst();
		assertThat(json).isNotEmpty();

		var xdsBuilder = new XdsBuilder();
		var listener = xdsBuilder.buildListener(json);
		assertThat(listener).isNotNull();
	}

	@Test
	void buildRoute() {
		var snapshot = parseNodeGroupSnapshot("test-envoy", "1.0.0");
		var json = snapshot.routes().getFirst();
		assertThat(json).isNotEmpty();

		var xdsBuilder = new XdsBuilder();
		var route = xdsBuilder.buildRouteConfiguration(json);
		assertThat(route).isNotNull();
	}

	@Test
	void buildCluster() {
		var snapshot = parseNodeGroupSnapshot("test-envoy", "1.0.0");
		var json = snapshot.clusters().getFirst();
		assertThat(json).isNotEmpty();

		var xdsBuilder = new XdsBuilder();
		var cluster = xdsBuilder.buildCluster(json);
		assertThat(cluster).isNotNull();
	}

}
