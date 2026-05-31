package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackTeamTagLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarStatAttackTeamTagLinkRepository extends JpaRepository<WarStatAttackTeamTagLink, UUID> {

    List<WarStatAttackTeamTagLink> findAllByTeamIdIn(Collection<UUID> teamIds);

    void deleteAllByTeamId(UUID teamId);

    void deleteAllByTagId(UUID tagId);
}
