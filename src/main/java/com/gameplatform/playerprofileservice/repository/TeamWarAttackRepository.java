package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamWarAttackRepository extends JpaRepository<TeamWarAttack, UUID> {

    List<TeamWarAttack> findAllByPlayerProfileIdOrderByTeamIndexAsc(UUID playerProfileId);
}
