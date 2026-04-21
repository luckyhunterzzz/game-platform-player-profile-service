package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroCreateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerProfileHeroFacade;
import com.gameplatform.playerprofileservice.utility.HeaderNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping
    public ResponseEntity<List<PlayerProfileHeroResponseDto>> getMyHeroes(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        return ResponseEntity.ok(playerProfileHeroFacade.getMyHeroes(userId, email));
    }

    @PostMapping
    public ResponseEntity<PlayerProfileHeroResponseDto> addHero(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @RequestBody @Valid PlayerProfileHeroCreateRequestDto request
    ) {
        return ResponseEntity.ok(playerProfileHeroFacade.addHero(userId, email, request));
    }

    @DeleteMapping("/{profileHeroId}")
    public ResponseEntity<Void> deleteHero(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @PathVariable UUID profileHeroId
    ) {
        playerProfileHeroFacade.deleteHero(userId, email, profileHeroId);
        return ResponseEntity.noContent().build();
    }
}
