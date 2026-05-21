package com.gameplatform.playerprofileservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "war_stat_attack_team")
public class WarStatAttackTeam {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "player_profile_id", nullable = false)
    private UUID playerProfileId;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "team_order", nullable = false)
    private Integer teamOrder;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
