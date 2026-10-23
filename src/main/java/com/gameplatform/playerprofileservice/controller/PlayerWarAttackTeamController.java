package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerWarAttackTeamsUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerWarAttackTeamsResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerWarAttackTeamFacade;
import com.gameplatform.playerprofileservice.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/me/war-attack-teams")
public class PlayerWarAttackTeamController {

    private final PlayerWarAttackTeamFacade playerWarAttackTeamFacade;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<PlayerWarAttackTeamsResponseDto> getMyTeams() {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarAttackTeamFacade.getMyTeams(
                currentUser.userId(),
                currentUser.email()
        ));
    }

    @PutMapping
    public ResponseEntity<PlayerWarAttackTeamsResponseDto> updateMyTeams(
            @RequestBody @Valid PlayerWarAttackTeamsUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(playerWarAttackTeamFacade.updateMyTeams(
                currentUser.userId(),
                currentUser.email(),
                request
        ));
    }
}
