package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackTeamSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarStatAttackTeamSlotRepository extends JpaRepository<WarStatAttackTeamSlot, UUID> {

    List<WarStatAttackTeamSlot> findAllByTeamId(UUID teamId);

    List<WarStatAttackTeamSlot> findAllByTeamIdIn(Collection<UUID> teamIds);

    void deleteAllByTeamId(UUID teamId);
}
