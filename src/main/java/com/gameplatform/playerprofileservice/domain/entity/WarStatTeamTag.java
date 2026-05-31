package com.gameplatform.playerprofileservice.domain.entity;

import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagCategory;
import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagScopeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "war_stat_team_tag")
public class WarStatTeamTag {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 16)
    private WarStatTeamTagScopeType scopeType;

    @Column(name = "player_profile_id")
    private UUID playerProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private WarStatTeamTagCategory category;

    @Column(name = "code", length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "icon_key", nullable = false, length = 128)
    private String iconKey;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
