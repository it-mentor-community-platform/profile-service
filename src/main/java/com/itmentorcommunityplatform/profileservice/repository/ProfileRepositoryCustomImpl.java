package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {

    private final JdbcTemplate template;

    @Override
    public void createProfile(Profile profile) {
        String profileCreation = """
                INSERT INTO profiles (telegram_user_id)
                VALUES (?)
                RETURNING id
                """;

        Long id = template.queryForObject(profileCreation, Long.class, profile.getTelegramUserId());

        String profileDetailsCreation = """
                INSERT INTO profiles_details (profile_id, detail_name, detail_value)
                VALUES (?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        for (ProfileDetail detail : profile.getDetails()) {
            template.update(profileDetailsCreation, id, detail.getDetailName(), detail.getDetailValue());
        }
    }

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

        for (ProfileDetail detail : profile.getDetails()) {
            boolean shouldUpdate = true;

            if (ProfileDetailType.GITHUB_PROFILE_URL.getDetailName()
                    .equals(detail.getDetailName()) ||
                    ProfileDetailType.TELEGRAM_URL.getDetailName()
                            .equals(detail.getDetailName())
            ) {

                boolean doesUrlExist = template.queryForObject(
                        fieldExistence,
                        Boolean.class,
                        detail.getDetailName(),
                        detail.getDetailValue(),
                        id
                );

                shouldUpdate = !doesUrlExist;

                //Todo подумать про конкурентность. (точнее про... рейс кондиишн)
                // но бизнес-требование «пропустить конфликтующий ЮРЛ и сохранить остальные детали»
                // при такой гонке может нарушиться из-за ошибки транзакции.
            }

            if (shouldUpdate) {
                template.update(updateDetails, id, detail.getDetailName(), detail.getDetailValue());
            }
        }
    }
}
