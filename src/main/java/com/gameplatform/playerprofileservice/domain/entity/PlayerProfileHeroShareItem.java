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

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_profile_hero_share_items")
public class PlayerProfileHeroShareItem {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "share_id", nullable = false)
    private UUID shareId;

    @Column(name = "hero_id", nullable = false)
    private Long heroId;

    @Enumerated(EnumType.STRING)
    @Column(name = "power_grade", nullable = false, length = 64)
    private HeroPowerGrade powerGrade;

    @Column(name = "talent_level", nullable = false)
    private Integer talentLevel;

    @Column(name = "costume_count", nullable = false)
    private Integer costumeCount;

    @Column(name = "item_order", nullable = false)
    private Integer itemOrder;
}
