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
@Table(name = "war_stat_attack_team_slot")
public class WarStatAttackTeamSlot {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "slot", nullable = false)
    private Short slot;

    @Column(name = "player_profile_hero_id", nullable = false)
    private UUID playerProfileHeroId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
