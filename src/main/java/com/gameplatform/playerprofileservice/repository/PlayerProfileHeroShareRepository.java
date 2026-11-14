package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerProfileHeroShareRepository extends JpaRepository<PlayerProfileHeroShare, UUID> {

    Optional<PlayerProfileHeroShare> findByToken(UUID token);
}
