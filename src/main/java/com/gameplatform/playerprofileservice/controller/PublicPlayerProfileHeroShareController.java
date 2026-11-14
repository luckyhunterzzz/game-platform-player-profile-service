package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroShareResponseDto;
import com.gameplatform.playerprofileservice.service.PlayerProfileHeroShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/profile-hero-shares")
public class PublicPlayerProfileHeroShareController {

    private final PlayerProfileHeroShareService playerProfileHeroShareService;

    @GetMapping("/{token}")
    public ResponseEntity<PlayerProfileHeroShareResponseDto> getPublicShare(@PathVariable UUID token) {
        return ResponseEntity.ok(playerProfileHeroShareService.getPublicShare(token));
    }
}
