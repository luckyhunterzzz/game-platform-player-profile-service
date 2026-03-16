package com.gameplatform.playerprofileservice.mapper;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerProfileMapper {
    PlayerProfileResponseDto toResponseDto(PlayerProfile playerProfile);
}