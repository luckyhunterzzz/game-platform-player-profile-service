package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.configuration.CacheNames;
import com.gameplatform.playerprofileservice.configuration.ProfileCacheEvictionService;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagCatalogResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarStatTeamTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarStatTeamTagFacade {

    private final PlayerWarStatTeamTagService playerWarStatTeamTagService;
    private final ProfileCacheEvictionService profileCacheEvictionService;

    @Cacheable(cacheNames = CacheNames.MY_WAR_STAT_TAG_CATALOG)
    public PlayerWarStatTeamTagCatalogResponseDto getCatalog(UUID userId, String email) {
        return toResponse(playerWarStatTeamTagService.getCatalog(userId, email));
    }

    public PlayerWarStatTeamTagCatalogResponseDto createCustomTag(UUID userId,
                                                                  String email,
                                                                  PlayerWarStatTeamTagUpsertRequestDto request) {
        PlayerWarStatTeamTagCatalogResponseDto response = toResponse(playerWarStatTeamTagService.createCustomTag(userId, email, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatTeamTagCatalogResponseDto updateCustomTag(UUID userId,
                                                                  String email,
                                                                  UUID tagId,
                                                                  PlayerWarStatTeamTagUpsertRequestDto request) {
        PlayerWarStatTeamTagCatalogResponseDto response = toResponse(playerWarStatTeamTagService.updateCustomTag(userId, email, tagId, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatTeamTagCatalogResponseDto deleteCustomTag(UUID userId, String email, UUID tagId) {
        PlayerWarStatTeamTagCatalogResponseDto response = toResponse(playerWarStatTeamTagService.deleteCustomTag(userId, email, tagId));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    private PlayerWarStatTeamTagCatalogResponseDto toResponse(PlayerWarStatTeamTagService.WarStatTeamTagCatalogView view) {
        return PlayerWarStatTeamTagCatalogResponseDto.builder()
                .teamTagLimit(view.teamTagLimit())
                .customTagLimit(view.customTagLimit())
                .items(view.items().stream()
                        .map(item -> PlayerWarStatTeamTagResponseDto.builder()
                                .id(item.id())
                                .scopeType(item.scopeType().name())
                                .category(item.category().name())
                                .code(item.code())
                                .name(item.name())
                                .iconKey(item.iconKey())
                                .imageUrl(item.imageUrl())
                                .build())
                        .toList())
                .build();
    }
}
