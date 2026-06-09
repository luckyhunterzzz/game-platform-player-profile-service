package com.gameplatform.playerprofileservice.configuration;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class ProfileCacheEvictionService {

    @CacheEvict(
            cacheNames = {
                    CacheNames.MY_PROFILE,
                    CacheNames.PROFILE_BY_USER_ID,
                    CacheNames.MY_PROFILE_HEROES,
                    CacheNames.MY_WAR_ATTACK_TEAMS,
                    CacheNames.MY_WAR_STAT_ATTACK_TEAMS,
                    CacheNames.MY_WAR_STAT_TAG_CATALOG
            },
            allEntries = true
    )
    public void evictAllProfileCaches() {
    }
}
