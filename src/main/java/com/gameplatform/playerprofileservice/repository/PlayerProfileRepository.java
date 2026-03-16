package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, UUID> {
    Optional<PlayerProfile> findByUserId(UUID userId);
}