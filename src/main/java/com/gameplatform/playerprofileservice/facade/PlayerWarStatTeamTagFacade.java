package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagCatalogResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarStatTeamTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarStatTeamTagFacade {

    private final PlayerWarStatTeamTagService playerWarStatTeamTagService;

    public PlayerWarStatTeamTagCatalogResponseDto getCatalog(UUID userId, String email) {
        return toResponse(playerWarStatTeamTagService.getCatalog(userId, email));
    }

    public PlayerWarStatTeamTagCatalogResponseDto createCustomTag(UUID userId,
                                                                  String email,
                                                                  PlayerWarStatTeamTagUpsertRequestDto request) {
        return toResponse(playerWarStatTeamTagService.createCustomTag(userId, email, request));
    }

    public PlayerWarStatTeamTagCatalogResponseDto updateCustomTag(UUID userId,
                                                                  String email,
                                                                  UUID tagId,
                                                                  PlayerWarStatTeamTagUpsertRequestDto request) {
        return toResponse(playerWarStatTeamTagService.updateCustomTag(userId, email, tagId, request));
    }

    public PlayerWarStatTeamTagCatalogResponseDto deleteCustomTag(UUID userId, String email, UUID tagId) {
        return toResponse(playerWarStatTeamTagService.deleteCustomTag(userId, email, tagId));
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
