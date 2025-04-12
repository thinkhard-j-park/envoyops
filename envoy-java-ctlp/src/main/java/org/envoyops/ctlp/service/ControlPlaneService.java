package org.envoyops.ctlp.service;

import com.linecorp.armeria.server.annotation.Put;
import io.envoyproxy.controlplane.cache.v3.SimpleCache;
import org.envoyops.ctlp.xds.NodeGroupSnapshot;
import org.envoyops.ctlp.xds.XdsBuilder;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ControlPlaneService implements MessageListener {

	private final RedisTemplate<String, NodeGroupSnapshot> redisTemplate;

	private final ChannelTopic topic;

	private final SimpleCache<String> snapshotCache;

	private final XdsBuilder xdsBuilder;

	public ControlPlaneService(RedisTemplate<String, NodeGroupSnapshot> redisTemplate, ChannelTopic topic,
			SimpleCache<String> snapshotCache, XdsBuilder xdsBuilder) {
		this.redisTemplate = redisTemplate;
		this.topic = topic;
		this.snapshotCache = snapshotCache;
		this.xdsBuilder = xdsBuilder;
	}

	@Put("/snapshot")
	public void publishSnapshot(NodeGroupSnapshot nodeGroupSnapshot) {
		this.redisTemplate.convertAndSend(this.topic.getTopic(), nodeGroupSnapshot);
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		var nodeGroupSnapshot = (NodeGroupSnapshot) this.redisTemplate.getValueSerializer()
			.deserialize(message.getBody());
		Assert.notNull(nodeGroupSnapshot, "nodeGroupSnapshot must not be null");
		var snapshot = this.xdsBuilder.buildSnapshot(nodeGroupSnapshot);
		this.snapshotCache.setSnapshot(nodeGroupSnapshot.nodeGroupId(), snapshot);
	}

}
