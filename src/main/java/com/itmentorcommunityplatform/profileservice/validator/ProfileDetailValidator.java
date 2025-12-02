package com.itmentorcommunityplatform.profileservice.validator;

public interface ProfileDetailValidator {
    /**
     * Возвращает detailName из ProfileDetailType, для которого этот валидатор предназначен.
     */
    String getProfileDetailTypeName();

    /**
     * Выполнить валидацию специфичную для конкретного ProfileDetailType.
     */
    void validate(String value);
}
