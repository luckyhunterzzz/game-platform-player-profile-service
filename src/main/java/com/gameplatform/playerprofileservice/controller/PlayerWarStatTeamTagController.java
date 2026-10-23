package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagCatalogResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarStatTeamTagFacade;
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
@RequestMapping("/api/v1/profile/me/war-stat-tags")
public class PlayerWarStatTeamTagController {

    private final PlayerWarStatTeamTagFacade playerWarStatTeamTagFacade;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> getCatalog() {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatTeamTagFacade.getCatalog(
                currentUser.userId(),
                currentUser.email()
        ));
    }

    @PostMapping
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> createCustomTag(
            @RequestBody @Valid PlayerWarStatTeamTagUpsertRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatTeamTagFacade.createCustomTag(
                currentUser.userId(),
                currentUser.email(),
                request
        ));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> updateCustomTag(
            @PathVariable UUID tagId,
            @RequestBody @Valid PlayerWarStatTeamTagUpsertRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatTeamTagFacade.updateCustomTag(
                currentUser.userId(),
                currentUser.email(),
                tagId,
                request
        ));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> deleteCustomTag(
            @PathVariable UUID tagId
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarStatTeamTagFacade.deleteCustomTag(
                currentUser.userId(),
                currentUser.email(),
                tagId
        ));
    }
}
