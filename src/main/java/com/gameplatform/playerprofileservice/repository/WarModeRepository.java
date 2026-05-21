package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarModeRepository extends JpaRepository<WarMode, UUID> {

    List<WarMode> findAllByActiveTrueOrderBySortOrderAsc();

    Optional<WarMode> findByCodeIgnoreCase(String code);
}
