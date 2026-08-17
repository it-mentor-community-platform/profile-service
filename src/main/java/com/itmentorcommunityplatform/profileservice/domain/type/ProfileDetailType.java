package com.itmentorcommunityplatform.profileservice.domain.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * <p> Перечисление всех разрешённых параметров профиля (details),
 * которые могут быть сохранены в системе. </p>
 * detailName совпадает с ключом во входящем JSON.
 * <p> Для добавления нового параметра профиля <br>
 * - добавьте новый элемент enum с уникальным detailName <br>
 * - при необходимости создайте валидатор имплементирующий
 * интерфейс ProfileDetailValidator и являющийся @Component
 * </p>
 * Пример:
 * GITHUB_PROFILE_URL("github_profile_url")
 * <p> Статический метод fromName() позволяет получить тип по его
 * detailName.</p>
 */
@Getter
public enum ProfileDetailType {

    GITHUB_PROFILE_URL("github_profile_url"),

    TELEGRAM_URL("telegram_url"),

    FIRST_NAME("first_name"),

    LAST_NAME("last_name");

    private final String detailName;

    ProfileDetailType(String detailName) {
        this.detailName = detailName;
    }

    /**
     * Возвращает Optional с соответствующим ProfileDetailType по его строковому detailName.
     */
    public static Optional<ProfileDetailType> fromName(String searchingName) {
        return Arrays.stream(values())
                .filter(type -> type.detailName.equals(searchingName))
                .findFirst();
    }
}
