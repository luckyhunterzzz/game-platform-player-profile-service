package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHero;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShare;
import com.gameplatform.playerprofileservice.domain.entity.PlayerProfileHeroShareItem;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroShareItemResponseDto;
import com.gameplatform.playerprofileservice.dto.response.PlayerProfileHeroShareResponseDto;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroRepository;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroShareItemRepository;
import com.gameplatform.playerprofileservice.repository.PlayerProfileHeroShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerProfileHeroShareService {

    private static final Set<Integer> ALLOWED_EXPIRATION_DAYS = Set.of(1, 3, 7);

    private final PlayerProfileService playerProfileService;
    private final PlayerProfileHeroRepository playerProfileHeroRepository;
    private final PlayerProfileHeroShareRepository playerProfileHeroShareRepository;
    private final PlayerProfileHeroShareItemRepository playerProfileHeroShareItemRepository;
    private final Clock clock;

    @Transactional
    public PlayerProfileHeroShareResponseDto createShare(
            UUID userId,
            String email,
            List<UUID> requestedHeroIds,
            Integer expiresInDays
    ) {
        validateExpiration(expiresInDays);
        validateUniqueHeroIds(requestedHeroIds);

        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        List<PlayerProfileHero> ownedHeroes = playerProfileHeroRepository
                .findAllByIdInAndPlayerProfileId(requestedHeroIds, playerProfile.getId());

        if (ownedHeroes.size() != requestedHeroIds.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "One or more profile heroes are invalid");
        }

        Map<UUID, PlayerProfileHero> heroesById = new HashMap<>();
        ownedHeroes.forEach(hero -> heroesById.put(hero.getId(), hero));

        OffsetDateTime now = OffsetDateTime.now(clock);
        PlayerProfileHeroShare share = playerProfileHeroShareRepository.save(
                PlayerProfileHeroShare.builder()
                        .id(UUID.randomUUID())
                        .token(UUID.randomUUID())
                        .playerProfileId(playerProfile.getId())
                        .createdAt(now)
                        .expiresAt(expiresInDays == null ? null : now.plusDays(expiresInDays))
                        .build()
        );

        List<PlayerProfileHeroShareItem> items = IntStream.range(0, requestedHeroIds.size())
                .mapToObj(index -> toShareItem(share.getId(), heroesById.get(requestedHeroIds.get(index)), index))
                .toList();
        playerProfileHeroShareItemRepository.saveAll(items);

        return toResponse(share, items);
    }

    public PlayerProfileHeroShareResponseDto getPublicShare(UUID token) {
        PlayerProfileHeroShare share = playerProfileHeroShareRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hero share was not found"));

        OffsetDateTime now = OffsetDateTime.now(clock);
        if (share.getExpiresAt() != null && !share.getExpiresAt().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.GONE, "Hero share has expired");
        }

        List<PlayerProfileHeroShareItem> items = playerProfileHeroShareItemRepository
                .findAllByShareIdOrderByItemOrderAsc(share.getId());
        return toResponse(share, items);
    }

    private void validateExpiration(Integer expiresInDays) {
        if (expiresInDays != null && !ALLOWED_EXPIRATION_DAYS.contains(expiresInDays)) {
            throw new ResponseStatusException(BAD_REQUEST, "Expiration must be 1, 3, 7 days or empty");
        }
    }

    private void validateUniqueHeroIds(List<UUID> requestedHeroIds) {
        if (new HashSet<>(requestedHeroIds).size() != requestedHeroIds.size()) {
            throw new ResponseStatusException(BAD_REQUEST, "Profile hero IDs must be unique");
        }
    }

    private PlayerProfileHeroShareItem toShareItem(UUID shareId, PlayerProfileHero hero, int order) {
        return PlayerProfileHeroShareItem.builder()
                .id(UUID.randomUUID())
                .shareId(shareId)
                .heroId(hero.getHeroId())
                .powerGrade(hero.getPowerGrade())
                .talentLevel(hero.getTalentLevel())
                .costumeCount(hero.getCostumeCount())
                .itemOrder(order)
                .build();
    }

    private PlayerProfileHeroShareResponseDto toResponse(
            PlayerProfileHeroShare share,
            List<PlayerProfileHeroShareItem> items
    ) {
        return new PlayerProfileHeroShareResponseDto(
                share.getToken(),
                share.getCreatedAt(),
                share.getExpiresAt(),
                items.stream()
                        .map(item -> new PlayerProfileHeroShareItemResponseDto(
                                item.getHeroId(),
                                item.getPowerGrade(),
                                item.getTalentLevel(),
                                item.getCostumeCount(),
                                item.getItemOrder()
                        ))
                        .toList()
        );
    }
}
