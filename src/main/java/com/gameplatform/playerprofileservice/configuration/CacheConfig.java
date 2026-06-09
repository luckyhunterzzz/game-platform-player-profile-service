package com.gameplatform.playerprofileservice.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private final RedisCacheErrorHandler redisCacheErrorHandler;

    public CacheConfig(RedisCacheErrorHandler redisCacheErrorHandler) {
        this.redisCacheErrorHandler = redisCacheErrorHandler;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .prefixCacheNameWith("player-profile-service::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(cacheObjectMapper)
                ));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            RedisCacheConfiguration redisCacheConfiguration,
            @Value("${app.cache.ttl.profile:PT30M}") Duration profileTtl,
            @Value("${app.cache.ttl.profile-heroes:PT30M}") Duration profileHeroesTtl,
            @Value("${app.cache.ttl.war-attack-teams:PT30M}") Duration warAttackTeamsTtl,
            @Value("${app.cache.ttl.war-stat-attack-teams:PT30M}") Duration warStatAttackTeamsTtl,
            @Value("${app.cache.ttl.war-stat-tags:PT30M}") Duration warStatTagsTtl
    ) {
        return builder -> builder
                .withCacheConfiguration(CacheNames.MY_PROFILE, redisCacheConfiguration.entryTtl(profileTtl))
                .withCacheConfiguration(CacheNames.PROFILE_BY_USER_ID, redisCacheConfiguration.entryTtl(profileTtl))
                .withCacheConfiguration(CacheNames.MY_PROFILE_HEROES, redisCacheConfiguration.entryTtl(profileHeroesTtl))
                .withCacheConfiguration(CacheNames.MY_WAR_ATTACK_TEAMS, redisCacheConfiguration.entryTtl(warAttackTeamsTtl))
                .withCacheConfiguration(CacheNames.MY_WAR_STAT_ATTACK_TEAMS, redisCacheConfiguration.entryTtl(warStatAttackTeamsTtl))
                .withCacheConfiguration(CacheNames.MY_WAR_STAT_TAG_CATALOG, redisCacheConfiguration.entryTtl(warStatTagsTtl));
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return redisCacheErrorHandler;
    }
}
