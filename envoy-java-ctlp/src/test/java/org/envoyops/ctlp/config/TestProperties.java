package org.envoyops.ctlp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "test")
public class TestProperties {

	private String envoyControlPlaneUrl;

	public String getEnvoyControlPlaneUrl() {
		return this.envoyControlPlaneUrl;
	}

	public void setEnvoyControlPlaneUrl(String envoyControlPlaneUrl) {
		this.envoyControlPlaneUrl = envoyControlPlaneUrl;
	}

}
