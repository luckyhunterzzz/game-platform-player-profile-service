package com.gameplatform.playerprofileservice.facade;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackImportRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackRecordUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarModeResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackRecordResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamSlotResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerWarStatAttackTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerWarStatAttackTeamFacade {

    private final PlayerWarStatAttackTeamService playerWarStatAttackTeamService;

    public PlayerWarStatAttackTeamsResponseDto getMyTeams(UUID userId, String email) {
        return toResponse(playerWarStatAttackTeamService.getMyTeams(userId, email));
    }

    public PlayerWarStatAttackTeamsResponseDto createTeam(UUID userId, String email) {
        return toResponse(playerWarStatAttackTeamService.createTeam(userId, email));
    }

    public PlayerWarStatAttackTeamsResponseDto importWarTeam(UUID userId,
                                                             String email,
                                                             PlayerWarStatAttackImportRequestDto request) {
        return toResponse(playerWarStatAttackTeamService.importWarTeam(userId, email, request));
    }

    public PlayerWarStatAttackTeamsResponseDto updateTeam(UUID userId,
                                                          String email,
                                                          UUID teamId,
                                                          PlayerWarStatAttackTeamUpdateRequestDto request) {
        return toResponse(playerWarStatAttackTeamService.updateTeam(userId, email, teamId, request));
    }

    public PlayerWarStatAttackTeamsResponseDto deleteTeam(UUID userId, String email, UUID teamId) {
        return toResponse(playerWarStatAttackTeamService.deleteTeam(userId, email, teamId));
    }

    public PlayerWarStatAttackTeamsResponseDto createRecord(UUID userId,
                                                            String email,
                                                            UUID teamId,
                                                            PlayerWarStatAttackRecordUpsertRequestDto request) {
        return toResponse(playerWarStatAttackTeamService.createRecord(userId, email, teamId, request));
    }

    public PlayerWarStatAttackTeamsResponseDto updateRecord(UUID userId,
                                                            String email,
                                                            UUID teamId,
                                                            UUID recordId,
                                                            PlayerWarStatAttackRecordUpsertRequestDto request) {
        return toResponse(playerWarStatAttackTeamService.updateRecord(userId, email, teamId, recordId, request));
    }

    public PlayerWarStatAttackTeamsResponseDto deleteRecord(UUID userId, String email, UUID teamId, UUID recordId) {
        return toResponse(playerWarStatAttackTeamService.deleteRecord(userId, email, teamId, recordId));
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
                                .build())
                        .toList())
                .build();
    }
}
