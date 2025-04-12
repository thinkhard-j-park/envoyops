package org.envoyops.ctlp.config;

import org.envoyops.ctlp.client.ControlPlaneOperation;
import org.envoyops.ctlp.client.HttpClient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({ TestProperties.class })
@Configuration
public class TestConfig {

	@Bean
	public ControlPlaneOperation controlPlaneOperation(TestProperties testProperties) {
		return new ControlPlaneOperation(
				HttpClient.restClient("envoy-ctlp-test", testProperties.getEnvoyControlPlaneUrl()));
	}

}
