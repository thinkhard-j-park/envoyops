package org.envoyops.ctlp.config;

import org.envoyops.ctlp.service.ControlPlaneService;
import org.envoyops.ctlp.xds.NodeGroupSnapshot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, NodeGroupSnapshot> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		var template = new RedisTemplate<String, NodeGroupSnapshot>();
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		var valueSerializer = new Jackson2JsonRedisSerializer<>(NodeGroupSnapshot.class);
		template.setKeySerializer(keySerializer);
		template.setValueSerializer(valueSerializer);
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public ChannelTopic envoyControlPlaneTopic(ControlPlaneProperties controlPlaneProperties) {
		return new ChannelTopic(controlPlaneProperties.getTopic());
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
			ControlPlaneService controlPlaneService, ChannelTopic channelTopic) {
		var container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(controlPlaneService, channelTopic);
		return container;
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter(ControlPlaneService controlPlaneService) {
		return new MessageListenerAdapter(controlPlaneService);
	}

}
