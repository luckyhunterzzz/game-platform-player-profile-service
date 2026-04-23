package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarAttackTeamFacade;
import com.gameplatform.playerprofileservice.utility.HeaderNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/me/war-attack-teams")
public class PlayerWarAttackTeamController {

    private final PlayerWarAttackTeamFacade playerWarAttackTeamFacade;

    @GetMapping
    public ResponseEntity<PlayerWarAttackTeamsResponseDto> getMyTeams(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        return ResponseEntity.ok(playerWarAttackTeamFacade.getMyTeams(userId, email));
    }

    @PutMapping
    public ResponseEntity<PlayerWarAttackTeamsResponseDto> updateMyTeams(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @RequestBody @Valid PlayerWarAttackTeamsUpdateRequestDto request
    ) {
        return ResponseEntity.ok(playerWarAttackTeamFacade.updateMyTeams(userId, email, request));
    }
}
