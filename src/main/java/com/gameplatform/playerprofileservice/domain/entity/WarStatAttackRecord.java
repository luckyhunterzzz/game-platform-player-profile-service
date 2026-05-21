package com.gameplatform.playerprofileservice.domain.entity;

import com.gameplatform.playerprofileservice.domain.enums.WarStatAttackResultType;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "war_stat_attack_record")
public class WarStatAttackRecord {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "war_mode_id", nullable = false)
    private UUID warModeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false, length = 64)
    private WarStatAttackResultType resultType;

    @Column(name = "battle_date", nullable = false)
    private LocalDate battleDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
