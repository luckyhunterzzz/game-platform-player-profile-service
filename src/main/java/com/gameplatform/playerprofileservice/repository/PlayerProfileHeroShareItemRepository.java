package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShareItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerProfileHeroShareItemRepository extends JpaRepository<PlayerProfileHeroShareItem, UUID> {

    List<PlayerProfileHeroShareItem> findAllByShareIdOrderByItemOrderAsc(UUID shareId);
}
