package org.envoyops.ctlp.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.envoyops.ctlp.client.ControlPlaneOperation;
import org.envoyops.ctlp.config.TestConfig;
import org.envoyops.ctlp.config.TestProperties;
import org.envoyops.ctlp.xds.NodeGroupSnapshot;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { TestConfig.class })
public abstract class AbstractControlPlaneTests {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractControlPlaneTests.class);

	@Autowired
	protected TestProperties testProperties;

	@Autowired
	protected ControlPlaneOperation controlPlaneOperation;

	protected List<String> readFiles(String path, String ext, String xdsType) {
		var files = new ArrayList<String>();

		var resourceDir = Path.of(path).toFile();
		if (resourceDir.exists()) {
			for (var file : Objects.requireNonNull(resourceDir.listFiles())) {
				if (file.getName().endsWith(ext) && file.getName().contains(xdsType)) {
					try {
						files.add(Files.readString(file.toPath()));
					}
					catch (IOException ex) {
						logger.error("Failed to read file: {}", file.getName(), ex);
					}
				}
			}

		}
		return files;
	}

	protected NodeGroupSnapshot parseNodeGroupSnapshot(String nodeGroupId, String version) {
		var clusterList = readFiles("src/test/resources/envoy/" + nodeGroupId, "json", "cluster");
		var listenerList = readFiles("src/test/resources/envoy/" + nodeGroupId, "json", "listener");
		var routeList = readFiles("src/test/resources/envoy/" + nodeGroupId, "json", "route");

		return NodeGroupSnapshot.newBuilder()
			.nodeGroupId(nodeGroupId)
			.clusters(clusterList)
			.listeners(listenerList)
			.routes(routeList)
			.version(version)
			.build();
	}

}
