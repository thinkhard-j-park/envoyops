package org.envoyops.ctlp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "envoyctlp")
public class ControlPlaneProperties {

	private String topic;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

}
