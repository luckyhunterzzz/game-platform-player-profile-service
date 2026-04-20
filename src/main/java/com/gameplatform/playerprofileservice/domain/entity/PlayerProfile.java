package com.gameplatform.playerprofileservice.domain.entity;

import com.gameplatform.playerprofileservice.domain.enums.PlayerProfileStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player_profiles")
public class PlayerProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "telegram_username")
    private String telegramUsername;

    @Column(name = "vk_username")
    private String vkUsername;

    @Column(name = "discord_username")
    private String discordUsername;

    @Column(name = "current_game_nickname")
    private String currentGameNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlayerProfileStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
