package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarStatAttackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarStatAttackRecordRepository extends JpaRepository<WarStatAttackRecord, UUID> {

    List<WarStatAttackRecord> findAllByTeamIdIn(Collection<UUID> teamIds);

    Optional<WarStatAttackRecord> findByIdAndTeamId(UUID id, UUID teamId);

    boolean existsByTeamId(UUID teamId);
}
