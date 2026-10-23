package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {

    private final JdbcTemplate template;

    @Transactional
    @Override
    public void createProfile(Profile profile) {
        String profileCreation = """
                INSERT INTO profiles (telegram_user_id)
                VALUES (?)
                RETURNING id
                """;

        Long id = template.queryForObject(profileCreation, Long.class, profile.getTelegramUserId());
        profile.setId(id);

        String profileDetailsCreation = """
                INSERT INTO profiles_details (profile_id, detail_name, detail_value)
                VALUES (?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        Set<ProfileDetail> successfullyChangedDetails = new HashSet<>();

        for (ProfileDetail detail : profile.getDetails()) {
            int updated = template.update(
                    profileDetailsCreation,
                    id,
                    detail.getDetailName(),
                    detail.getDetailValue()
            );

            if (updated > 0) {
                successfullyChangedDetails.add(detail);
            }
        }

        profile.setDetails(successfullyChangedDetails);
    }

    @Transactional
    @Override
    public void updateProfile(Profile profile, Long id) {
        String updateDetails = """
                INSERT INTO profiles_details (profile_id, detail_name, detail_value)
                VALUES (?, ?, ?)
                ON CONFLICT (profile_id, detail_name)
                DO UPDATE
                SET detail_value = EXCLUDED.detail_value;
                """;

        String fieldExistence = """
                        SELECT EXISTS (
                        SELECT 1
                        FROM profiles_details
                        WHERE detail_name = ?
                        AND detail_value = ?
                        AND profile_id <> ?
                        )
                        """;

        Set<ProfileDetail> successfullyChangedDetails = new HashSet<>();

        for (ProfileDetail detail : profile.getDetails()) {
            boolean shouldUpdate = true;

            if (doesUrlMatch(detail)) {
                boolean doesUrlExist = template.queryForObject(
                        fieldExistence,
                        Boolean.class,
                        detail.getDetailName(),
                        detail.getDetailValue(),
                        id
                );

                shouldUpdate = !doesUrlExist;
            }

            if (shouldUpdate) {
                int updated = template.update(updateDetails, id, detail.getDetailName(), detail.getDetailValue());
                if (updated > 0) {
                    successfullyChangedDetails.add(detail);
                }
            }
        }

        profile.setDetails(successfullyChangedDetails);
    }

    private boolean doesUrlMatch(ProfileDetail detail) {
        return ProfileDetailType.GITHUB_PROFILE_URL.getDetailName()
                .equals(detail.getDetailName()) ||
                ProfileDetailType.TELEGRAM_URL.getDetailName()
                        .equals(detail.getDetailName());
    }
}