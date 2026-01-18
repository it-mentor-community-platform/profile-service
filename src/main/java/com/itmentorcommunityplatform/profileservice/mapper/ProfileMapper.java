package com.itmentorcommunityplatform.profileservice.mapper;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileAchievementsResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileDetailsResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileNoAchievementsResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileNoIdResponseDto;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProfileMapper {


    ProfileNoIdResponseDto mapToProfileNoIdDto(Set<ProfileDetail> details, List<Achievement> achievements);

    ProfileNoAchievementsResponseDto mapToProfileNoAchievementsDto(Long telegramUserId, Set<ProfileDetail> details);

    List<ProfileAchievementsResponseDto> mapToProfileAchievementsDtoList(List<Achievement> achievements);

    default ProfileDetailsResponseDto mapToProfileDetailsDto(Set<ProfileDetail> details) {

        HashMap<String, Object> map = new HashMap<>();

        details.forEach(detail -> map.put(detail.getDetailName(), detail.getDetailValue()));

        return new ProfileDetailsResponseDto(map);
    }

}
