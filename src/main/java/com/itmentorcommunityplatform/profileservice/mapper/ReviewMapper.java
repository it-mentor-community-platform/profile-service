package com.itmentorcommunityplatform.profileservice.mapper;

import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ReviewCreatedEvent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "reviewerTelegramUserId", target = "telegramUserId")
    AchievementDto achievementDto(ReviewCreatedEvent reviewCreatedEvent);
}
