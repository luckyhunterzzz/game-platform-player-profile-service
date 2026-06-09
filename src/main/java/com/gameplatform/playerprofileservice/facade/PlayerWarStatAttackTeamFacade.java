package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.configuration.CacheNames;
import com.gameplatform.playerprofileservice.configuration.ProfileCacheEvictionService;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackImportRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackRecordUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamsReorderRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarModeResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackRecordResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamSlotResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarStatAttackTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarStatAttackTeamFacade {

    private final PlayerWarStatAttackTeamService playerWarStatAttackTeamService;
    private final ProfileCacheEvictionService profileCacheEvictionService;

    @Cacheable(cacheNames = CacheNames.MY_WAR_STAT_ATTACK_TEAMS)
    public PlayerWarStatAttackTeamsResponseDto getMyTeams(UUID userId, String email) {
        return toResponse(playerWarStatAttackTeamService.getMyTeams(userId, email));
    }

    public PlayerWarStatAttackTeamsResponseDto createTeam(UUID userId, String email) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.createTeam(userId, email));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto importWarTeam(UUID userId,
                                                             String email,
                                                             PlayerWarStatAttackImportRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.importWarTeam(userId, email, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto updateTeam(UUID userId,
                                                          String email,
                                                          UUID teamId,
                                                          PlayerWarStatAttackTeamUpdateRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.updateTeam(userId, email, teamId, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto updateTeamTags(UUID userId,
                                                              String email,
                                                              UUID teamId,
                                                              PlayerWarStatTeamTagsUpdateRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.updateTeamTags(userId, email, teamId, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto reorderTeams(UUID userId,
                                                            String email,
                                                            PlayerWarStatAttackTeamsReorderRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.reorderTeams(userId, email, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto deleteTeam(UUID userId, String email, UUID teamId) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.deleteTeam(userId, email, teamId));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto createRecord(UUID userId,
                                                            String email,
                                                            UUID teamId,
                                                            PlayerWarStatAttackRecordUpsertRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.createRecord(userId, email, teamId, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto updateRecord(UUID userId,
                                                            String email,
                                                            UUID teamId,
                                                            UUID recordId,
                                                            PlayerWarStatAttackRecordUpsertRequestDto request) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.updateRecord(userId, email, teamId, recordId, request));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    public PlayerWarStatAttackTeamsResponseDto deleteRecord(UUID userId, String email, UUID teamId, UUID recordId) {
        PlayerWarStatAttackTeamsResponseDto response = toResponse(playerWarStatAttackTeamService.deleteRecord(userId, email, teamId, recordId));
        profileCacheEvictionService.evictAllProfileCaches();
        return response;
    }

    private PlayerWarStatAttackTeamsResponseDto toResponse(PlayerWarStatAttackTeamService.WarStatAttackTeamsView view) {
        return PlayerWarStatAttackTeamsResponseDto.builder()
                .warModes(view.warModes().stream()
                        .map(mode -> PlayerWarModeResponseDto.builder()
                                .code(mode.code())
                                .nameRu(mode.nameRu())
                                .nameEn(mode.nameEn())
                                .descriptionRu(mode.descriptionRu())
                                .descriptionEn(mode.descriptionEn())
                                .sortOrder(mode.sortOrder())
                                .build())
                        .toList())
                .teams(view.teams().stream()
                        .map(team -> PlayerWarStatAttackTeamResponseDto.builder()
                                .id(team.id())
                                .name(team.name())
                                .teamOrder(team.teamOrder())
                                .slots(team.slots().stream()
                                        .map(slot -> PlayerWarStatAttackTeamSlotResponseDto.builder()
                                                .slot(slot.slot())
                                                .playerProfileHeroId(slot.playerProfileHeroId())
                                                .build())
                                        .toList())
                                .records(team.records().stream()
                                        .map(record -> PlayerWarStatAttackRecordResponseDto.builder()
                                                .id(record.id())
                                                .warModeCode(record.warModeCode())
                                                .resultType(record.resultType())
                                                .battleDate(record.battleDate())
                                                .build())
                                        .toList())
                                .tags(team.tags().stream()
                                        .map(tag -> PlayerWarStatTeamTagResponseDto.builder()
                                                .id(tag.id())
                                                .scopeType(tag.scopeType())
                                                .category(tag.category())
                                                .code(tag.code())
                                                .name(tag.name())
                                                .iconKey(tag.iconKey())
                                                .imageUrl(tag.imageUrl())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }
}
