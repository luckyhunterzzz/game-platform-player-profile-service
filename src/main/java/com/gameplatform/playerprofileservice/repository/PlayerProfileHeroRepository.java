package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerProfileHeroRepository extends JpaRepository<PlayerProfileHero, UUID> {

    List<PlayerProfileHero> findAllByPlayerProfileIdOrderByCreatedAtAsc(UUID playerProfileId);

    Optional<PlayerProfileHero> findByIdAndPlayerProfileId(UUID id, UUID playerProfileId);
}
