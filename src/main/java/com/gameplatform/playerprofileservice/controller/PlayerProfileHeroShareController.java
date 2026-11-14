package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerProfileHeroShareCreateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroShareResponseDto;
import com.gameplatform.playerprofileservice.security.CurrentUserProvider;
import com.gameplatform.playerprofileservice.service.PlayerProfileHeroShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class PlayerProfileHeroShareController {

    private final PlayerProfileHeroShareService playerProfileHeroShareService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/me/hero-shares")
    public ResponseEntity<PlayerProfileHeroShareResponseDto> createShare(
            @RequestBody @Valid PlayerProfileHeroShareCreateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerProfileHeroShareService.createShare(
                currentUser.userId(),
                currentUser.email(),
                request.profileHeroIds(),
                request.expiresInDays()
        ));
    }

}
