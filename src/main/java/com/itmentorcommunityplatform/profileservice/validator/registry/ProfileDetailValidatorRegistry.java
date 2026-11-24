package com.itmentorcommunityplatform.profileservice.validator.registry;

import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.validator.ProfileDetailValidator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Реестр всех специфичных валидаторов параметров профиля.</p>
 *
 * <p>Registry автоматически собирает все реализации ProfileDetailValidator,
 * которые находятся в Spring-контексте (отмечены @Component),
 * и сопоставляет их с конкретным ProfileDetailType.</p>
 *
 * <p>Основное назначение: <br>
 * - быстро определить, существует ли специфичный валидатор для конкретного ProfileDetailType; <br>
 * - предоставить валидатор сервисам, которые выполняют валидацию details.</p>
 *
 * <p>Добавление нового валидатора: <br>
 * 1. Создать класс, реализующий ProfileDetailValidator. <br>
 * 2. Вернуть корректный ProfileDetailType в getSupportedProfileDetailType(). <br>
 * 3. Отметить класс @Component. <br>
 * 4. Registry автоматически подхватит его.</p>
 */
@Component
public class ProfileDetailValidatorRegistry {
    private final Map<ProfileDetailType, ProfileDetailValidator> validatorsMap = new HashMap<>();

    public ProfileDetailValidatorRegistry(List<ProfileDetailValidator> validators) {
        for (ProfileDetailValidator v : validators) {
            validatorsMap.put(v.getSupportedProfileDetailType(), v);
        }
    }

    /**
     * Возвращает специфичный валидатор для указанного ProfileDetailType,
     * если он зарегистрирован.
     */
    public Optional<ProfileDetailValidator> getSpecificValidator(ProfileDetailType detailType) {
        return Optional.ofNullable(validatorsMap.get(detailType));
    }
}
