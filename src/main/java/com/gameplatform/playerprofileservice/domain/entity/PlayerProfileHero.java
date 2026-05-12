package com.gameplatform.playerprofileservice.domain.entity;

import com.gameplatform.playerprofileservice.domain.enums.HeroPowerGrade;
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
@Table(name = "player_profile_heroes")
public class PlayerProfileHero {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "player_profile_id", nullable = false)
    private UUID playerProfileId;

    @Column(name = "hero_id", nullable = false)
    private Long heroId;

    @Enumerated(EnumType.STRING)
    @Column(name = "power_grade", nullable = false, length = 64)
    private HeroPowerGrade powerGrade;

    @Column(name = "talent_level", nullable = false)
    private Integer talentLevel;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
