package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.TeamWarAttackHero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TeamWarAttackHeroRepository extends JpaRepository<TeamWarAttackHero, UUID> {

    List<TeamWarAttackHero> findAllByTeamWarAttackIdIn(Collection<UUID> teamWarAttackIds);

    void deleteAllByTeamWarAttackIdIn(Collection<UUID> teamWarAttackIds);
}
