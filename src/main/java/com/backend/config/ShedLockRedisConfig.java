package com.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.javacrumbs.shedlock.core.LockProvider;

@Configuration
public class ShedLockRedisConfig {
	@Bean
	public LockProvider lockProvider(org.springframework.data.redis.connection.RedisConnectionFactory cf) {
		return new net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider(cf, "timespace");
	}
}