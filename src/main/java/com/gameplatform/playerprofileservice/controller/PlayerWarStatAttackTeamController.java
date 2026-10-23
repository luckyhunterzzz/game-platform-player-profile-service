package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackImportRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackRecordUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatAttackTeamsReorderRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarStatAttackTeamFacade;
import com.gameplatform.playerprofileservice.security.CurrentUserProvider;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/me/war-stat-attack-teams")
public class PlayerWarStatAttackTeamController {

    private final PlayerWarStatAttackTeamFacade playerWarStatAttackTeamFacade;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> getMyTeams() {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.getMyTeams(
                currentUser.userId(),
                currentUser.email()
        ));
    }

    @PostMapping
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> createTeam() {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.createTeam(
                currentUser.userId(),
                currentUser.email()
        ));
    }

    @PostMapping("/import-war-team")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> importWarTeam(
            @RequestBody @Valid PlayerWarStatAttackImportRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.importWarTeam(
                currentUser.userId(),
                currentUser.email(),
                request
        ));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> updateTeam(
            @PathVariable UUID teamId,
            @RequestBody @Valid PlayerWarStatAttackTeamUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.updateTeam(
                currentUser.userId(),
                currentUser.email(),
                teamId,
                request
        ));
    }

    @PutMapping("/{teamId}/tags")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> updateTeamTags(
            @PathVariable UUID teamId,
            @RequestBody @Valid PlayerWarStatTeamTagsUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.updateTeamTags(
                currentUser.userId(),
                currentUser.email(),
                teamId,
                request
        ));
    }

    @PutMapping("/order")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> reorderTeams(
            @RequestBody @Valid PlayerWarStatAttackTeamsReorderRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.reorderTeams(
                currentUser.userId(),
                currentUser.email(),
                request
        ));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> deleteTeam(
            @PathVariable UUID teamId
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.deleteTeam(
                currentUser.userId(),
                currentUser.email(),
                teamId
        ));
    }

    @PostMapping("/{teamId}/records")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> createRecord(
            @PathVariable UUID teamId,
            @RequestBody @Valid PlayerWarStatAttackRecordUpsertRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.createRecord(
                currentUser.userId(),
                currentUser.email(),
                teamId,
                request
        ));
    }

    @PutMapping("/{teamId}/records/{recordId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> updateRecord(
            @PathVariable UUID teamId,
            @PathVariable UUID recordId,
            @RequestBody @Valid PlayerWarStatAttackRecordUpsertRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.updateRecord(
                currentUser.userId(),
                currentUser.email(),
                teamId,
                recordId,
                request
        ));
    }

    @DeleteMapping("/{teamId}/records/{recordId}")
    public ResponseEntity<PlayerWarStatAttackTeamsResponseDto> deleteRecord(
            @PathVariable UUID teamId,
            @PathVariable UUID recordId
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatAttackTeamFacade.deleteRecord(
                currentUser.userId(),
                currentUser.email(),
                teamId,
                recordId
        ));
    }
}
