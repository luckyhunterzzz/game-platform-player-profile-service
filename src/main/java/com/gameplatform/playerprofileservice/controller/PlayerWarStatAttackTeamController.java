package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackImportRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackRecordUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarStatAttackTeamFacade;
import com.gameplatform.playerprofileservice.utility.HeaderNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/me/war-stat-attack-teams")
public class PlayerWarStatAttackTeamController {

    private final PlayerWarStatAttackTeamFacade playerWarStatAttackTeamFacade;

    @GetMapping
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> getMyTeams(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.getMyTeams(userId, email));
    }

    @PostMapping
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> createTeam(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.createTeam(userId, email));
    }

    @PostMapping("/import-war-team")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> importWarTeam(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @RequestBody @Valid PlayerWarStatAttackImportRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.importWarTeam(userId, email, request));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> updateTeam(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID teamId,
            @RequestBody @Valid PlayerWarStatAttackTeamUpdateRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.updateTeam(userId, email, teamId, request));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> deleteTeam(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID teamId
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.deleteTeam(userId, email, teamId));
    }

    @PostMapping("/{teamId}/records")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> createRecord(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID teamId,
            @RequestBody @Valid PlayerWarStatAttackRecordUpsertRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.createRecord(userId, email, teamId, request));
    }

    @PutMapping("/{teamId}/records/{recordId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> updateRecord(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID teamId,
            @PathVariable UUID recordId,
            @RequestBody @Valid PlayerWarStatAttackRecordUpsertRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.updateRecord(userId, email, teamId, recordId, request));
    }

    @DeleteMapping("/{teamId}/records/{recordId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> deleteRecord(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID teamId,
            @PathVariable UUID recordId
    ) {
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.deleteRecord(userId, email, teamId, recordId));
    }
}
