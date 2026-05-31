package com.gameplatform.playerprofileservice.repository;

import com.gameplatform.playerprofileservice.domain.entity.WarStatTeamTag;
import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagCategory;
import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagScopeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarStatTeamTagRepository extends JpaRepository<WarStatTeamTag, UUID> {

    List<WarStatTeamTag> findAllByScopeTypeOrderByCategoryAscNameAsc(WarStatTeamTagScopeType scopeType);

    List<WarStatTeamTag> findAllByPlayerProfileIdOrderByNameAsc(UUID playerProfileId);

    List<WarStatTeamTag> findAllByIdIn(Collection<UUID> ids);

    Optional<WarStatTeamTag> findByIdAndPlayerProfileId(UUID id, UUID playerProfileId);

    Optional<WarStatTeamTag> findByScopeTypeAndCategoryAndCode(WarStatTeamTagScopeType scopeType,
                                                               WarStatTeamTagCategory category,
                                                               String code);

    boolean existsByPlayerProfileIdAndNameIgnoreCase(UUID playerProfileId, String name);

    long countByPlayerProfileId(UUID playerProfileId);
}
