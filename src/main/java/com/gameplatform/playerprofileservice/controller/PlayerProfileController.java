package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerProfileFacade;
import com.gameplatform.playerprofileservice.security.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/profile")
public class PlayerProfileController {

    private final PlayerProfileFacade playerProfileFacade;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/me/init")
    public ResponseEntity<PlayerProfileResponseDto> initializeMyProfile() {
        var currentUser = currentUserProvider.getCurrentUser();
        PlayerProfileResponseDto response = playerProfileFacade.initializeMyProfile(
                currentUser.userId(),
                currentUser.email()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PlayerProfileResponseDto> getMyProfile() {
        var currentUser = currentUserProvider.getCurrentUser();
        PlayerProfileResponseDto response = playerProfileFacade.getMyProfile(
                currentUser.userId(),
                currentUser.email()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<PlayerProfileResponseDto> updateMyProfile(
            @RequestBody @Valid PlayerProfileUpdateRequestDto request
    ) {
        var currentUser = currentUserProvider.getCurrentUser();
        PlayerProfileResponseDto response = playerProfileFacade.updateMyProfile(
                currentUser.userId(),
                currentUser.email(),
                request
        );
        return ResponseEntity.ok(response);
    }
}
