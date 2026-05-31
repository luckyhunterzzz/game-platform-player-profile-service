package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagUpsertRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarStatTeamTagCatalogResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarStatTeamTagFacade;
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
@RequestMapping("/api/v1/profile/me/war-stat-tags")
public class PlayerWarStatTeamTagController {

    private final PlayerWarStatTeamTagFacade playerWarStatTeamTagFacade;

    @GetMapping
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> getCatalog(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        return ResponseEntity.ok(playerWarStatTeamTagFacade.getCatalog(userId, email));
    }

    @PostMapping
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> createCustomTag(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @RequestBody @Valid PlayerWarStatTeamTagUpsertRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatTeamTagFacade.createCustomTag(userId, email, request));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> updateCustomTag(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID tagId,
            @RequestBody @Valid PlayerWarStatTeamTagUpsertRequestDto request
    ) {
        return ResponseEntity.ok(playerWarStatTeamTagFacade.updateCustomTag(userId, email, tagId, request));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<PlayerWarStatTeamTagCatalogResponseDto> deleteCustomTag(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID tagId
    ) {
        return ResponseEntity.ok(playerWarStatTeamTagFacade.deleteCustomTag(userId, email, tagId));
    }
}
