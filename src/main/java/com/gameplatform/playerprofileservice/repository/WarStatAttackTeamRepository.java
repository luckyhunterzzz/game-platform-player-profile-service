package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarStatAttackTeamRepository extends JpaRepository<WarStatAttackTeam, UUID> {

    List<WarStatAttackTeam> findAllByPlayerProfileIdOrderByTeamOrderAsc(UUID playerProfileId);

    Optional<WarStatAttackTeam> findByIdAndPlayerProfileId(UUID id, UUID playerProfileId);
}
