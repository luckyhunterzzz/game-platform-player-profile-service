package com.gameplatform.playerprofileservice.controller;

import com.gameplatform.playerprofileservice.dto.request.PlayerProfileUpdateRequestDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import com.gameplatform.playerprofileservice.facade.PlayerProfileFacade;
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
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/profile")
public class PlayerProfileController {

    private final PlayerProfileFacade playerProfileFacade;

    @GetMapping("/me")
    public ResponseEntity<PlayerProfileResponseDto> getMyProfile(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email
    ) {
        PlayerProfileResponseDto response = playerProfileFacade.getOrCreateMyProfile(userId, email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<PlayerProfileResponseDto> updateMyProfile(
            @RequestHeader(HeaderNames.USER_ID) UUID userId,
            @RequestHeader(HeaderNames.USER_EMAIL) String email,
            @RequestBody @Valid PlayerProfileUpdateRequestDto request
    ) {
        PlayerProfileResponseDto response = playerProfileFacade.updateMyProfile(userId, email, request);
        return ResponseEntity.ok(response);
    }
}
