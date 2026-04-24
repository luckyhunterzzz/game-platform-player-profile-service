package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerProfileFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/player-profiles")
public class InternalPlayerProfileController {

    private final PlayerProfileFacade playerProfileFacade;

    @GetMapping("/{userId}")
    public ResponseEntity<PlayerProfileResponseDto> getProfileByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(playerProfileFacade.getProfileByUserId(userId));
    }
}
