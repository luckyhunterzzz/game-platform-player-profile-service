package com.gameplatform.playerprofileservice.service;

import com.gameplatform.playerprofileservice.domain.entity.PlayerProfile;
import com.gameplatform.playerprofileservice.domain.entity.WarStatTeamTag;
import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagCategory;
import com.gameplatform.playerprofileservice.domain.enums.WarStatTeamTagScopeType;
import com.gameplatform.playerprofileservice.dto.request.PlayerWarStatTeamTagUpsertRequestDto;
import com.gameplatform.playerprofileservice.repository.WarStatAttackTeamTagLinkRepository;
import com.gameplatform.playerprofileservice.repository.WarStatTeamTagRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerWarStatTeamTagService {

    public static final int TEAM_TAG_LIMIT = 7;
    public static final int CUSTOM_TAG_LIMIT = 100;

    private final PlayerProfileService playerProfileService;
    private final WarStatTeamTagRepository warStatTeamTagRepository;
    private final WarStatAttackTeamTagLinkRepository warStatAttackTeamTagLinkRepository;
    private final Clock clock;

    public WarStatTeamTagCatalogView getCatalog(UUID userId, String email) {
        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        return buildCatalogView(playerProfile.getId());
    }

    @Transactional
    public WarStatTeamTagCatalogView createCustomTag(UUID userId,
                                                     String email,
                                                     PlayerWarStatTeamTagUpsertRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        validateCustomTagRequest(playerProfile.getId(), null, request);

        OffsetDateTime now = OffsetDateTime.now(clock);
        warStatTeamTagRepository.save(WarStatTeamTag.builder()
                .id(UUID.randomUUID())
                .scopeType(WarStatTeamTagScopeType.CUSTOM)
                .playerProfileId(playerProfile.getId())
                .category(WarStatTeamTagCategory.CUSTOM)
                .code(null)
                .name(request.name().trim())
                .iconKey(request.iconKey().trim())
                .imageUrl(request.imageUrl().trim())
                .createdAt(now)
                .updatedAt(now)
                .build());

        return buildCatalogView(playerProfile.getId());
    }

    @Transactional
    public WarStatTeamTagCatalogView updateCustomTag(UUID userId,
                                                     String email,
                                                     UUID tagId,
                                                     PlayerWarStatTeamTagUpsertRequestDto request) {
        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        WarStatTeamTag tag = warStatTeamTagRepository.findByIdAndPlayerProfileId(tagId, playerProfile.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War statistic team tag not found"));

        validateCustomTagRequest(playerProfile.getId(), tag.getId(), request);

        tag.setName(request.name().trim());
        tag.setIconKey(request.iconKey().trim());
        tag.setImageUrl(request.imageUrl().trim());
        tag.setUpdatedAt(OffsetDateTime.now(clock));
        warStatTeamTagRepository.save(tag);

        return buildCatalogView(playerProfile.getId());
    }

    @Transactional
    public WarStatTeamTagCatalogView deleteCustomTag(UUID userId, String email, UUID tagId) {
        PlayerProfile playerProfile = playerProfileService.getRequiredProfile(userId, email);
        WarStatTeamTag tag = warStatTeamTagRepository.findByIdAndPlayerProfileId(tagId, playerProfile.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "War statistic team tag not found"));

        warStatAttackTeamTagLinkRepository.deleteAllByTagId(tag.getId());
        warStatTeamTagRepository.delete(tag);

        return buildCatalogView(playerProfile.getId());
    }

    private WarStatTeamTagCatalogView buildCatalogView(UUID playerProfileId) {
        List<WarStatTeamTagView> items = loadAvailableTags(playerProfileId).stream()
                .map(this::toView)
                .toList();

        return WarStatTeamTagCatalogView.builder()
                .teamTagLimit(TEAM_TAG_LIMIT)
                .customTagLimit(CUSTOM_TAG_LIMIT)
                .items(items)
                .build();
    }

    public List<WarStatTeamTag> loadAvailableTags(UUID playerProfileId) {
        return java.util.stream.Stream.concat(
                        warStatTeamTagRepository.findAllByScopeTypeOrderByCategoryAscNameAsc(WarStatTeamTagScopeType.SYSTEM).stream(),
                        warStatTeamTagRepository.findAllByPlayerProfileIdOrderByNameAsc(playerProfileId).stream()
                )
                .sorted(Comparator
                        .comparing((WarStatTeamTag tag) -> tag.getScopeType() == WarStatTeamTagScopeType.SYSTEM ? 0 : 1)
                        .thenComparing(WarStatTeamTag::getCategory)
                        .thenComparing(tag -> tag.getCode() == null ? "" : tag.getCode())
                        .thenComparing(WarStatTeamTag::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public WarStatTeamTag findSystemTagOrThrow(WarStatTeamTagCategory category, String code) {
        return warStatTeamTagRepository.findByScopeTypeAndCategoryAndCode(
                        WarStatTeamTagScopeType.SYSTEM,
                        category,
                        code.trim().toUpperCase()
                )
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "System war statistic tag not found: " + code));
    }

    private void validateCustomTagRequest(UUID playerProfileId,
                                          UUID currentTagId,
                                          PlayerWarStatTeamTagUpsertRequestDto request) {
        String normalizedName = request.name().trim();
        String normalizedIconKey = request.iconKey().trim();
        String normalizedImageUrl = request.imageUrl().trim();

        if (normalizedName.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Tag name is required");
        }
        if (normalizedIconKey.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Tag icon key is required");
        }
        if (normalizedImageUrl.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Tag image url is required");
        }

        if (currentTagId == null && warStatTeamTagRepository.countByPlayerProfileId(playerProfileId) >= CUSTOM_TAG_LIMIT) {
            throw new ResponseStatusException(BAD_REQUEST, "Custom tags limit reached");
        }

        boolean duplicateExists = warStatTeamTagRepository.findAllByPlayerProfileIdOrderByNameAsc(playerProfileId).stream()
                .anyMatch(tag -> !tag.getId().equals(currentTagId) && tag.getName().equalsIgnoreCase(normalizedName));
        if (duplicateExists) {
            throw new ResponseStatusException(BAD_REQUEST, "Custom tag with this name already exists");
        }
    }

    private WarStatTeamTagView toView(WarStatTeamTag tag) {
        return WarStatTeamTagView.builder()
                .id(tag.getId())
                .scopeType(tag.getScopeType())
                .category(tag.getCategory())
                .code(tag.getCode())
                .name(tag.getName())
                .iconKey(tag.getIconKey())
                .imageUrl(tag.getImageUrl())
                .build();
    }

    @Builder
    public record WarStatTeamTagView(
            UUID id,
            WarStatTeamTagScopeType scopeType,
            WarStatTeamTagCategory category,
            String code,
            String name,
            String iconKey,
            String imageUrl
    ) {
    }

    @Builder
    public record WarStatTeamTagCatalogView(
            Integer teamTagLimit,
            Integer customTagLimit,
            List<WarStatTeamTagView> items
    ) {
    }
}
