package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroCreateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroPowerGradeUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroTalentLevelUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerProfileHeroFacade;
import com.gameplatform.playerprofileservice.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/me/heroes")
public class PlayerProfileHeroController {

    private final PlayerProfileHeroFacade playerProfileHeroFacade;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<PlayerProfileHeroResponseDto>> getMyHeroes() {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerProfileHeroFacade.getMyHeroes(
                currentUser.userId(),
                currentUser.email()
        ));
    }

    @PostMapping
    public ResponseEntity<PlayerProfileHeroResponseDto> addHero(
            @RequestBody @Valid PlayerProfileHeroCreateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerProfileHeroFacade.addHero(
                currentUser.userId(),
                currentUser.email(),
                request
        ));
    }

    @PutMapping("/{profileHeroId}/power-grade")
    public ResponseEntity<PlayerProfileHeroResponseDto> updateHeroPowerGrade(
            @PathVariable UUID profileHeroId,
            @RequestBody @Valid PlayerProfileHeroPowerGradeUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerProfileHeroFacade.updateHeroPowerGrade(
                currentUser.userId(),
                currentUser.email(),
                profileHeroId,
                request
        ));
    }

    @PutMapping("/{profileHeroId}/talent-level")
    public ResponseEntity<PlayerProfileHeroResponseDto> updateHeroTalentLevel(
            @PathVariable UUID profileHeroId,
            @RequestBody @Valid PlayerProfileHeroTalentLevelUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerProfileHeroFacade.updateHeroTalentLevel(
                currentUser.userId(),
                currentUser.email(),
                profileHeroId,
                request
        ));
    }

    @DeleteMapping("/{profileHeroId}")
    public ResponseEntity<Void> deleteHero(
            @PathVariable UUID profileHeroId
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        playerProfileHeroFacade.deleteHero(currentUser.userId(), currentUser.email(), profileHeroId);
        return ResponseEntity.noContent().build();
    }
}
