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
@Table(name = "team_war_attack_heroes")
public class TeamWarAttackHero {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "team_war_attack_id", nullable = false)
    private UUID teamWarAttackId;

    @Column(name = "player_profile_hero_id", nullable = false)
    private UUID playerProfileHeroId;

    @Column(name = "slot", nullable = false)
    private Short slot;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
