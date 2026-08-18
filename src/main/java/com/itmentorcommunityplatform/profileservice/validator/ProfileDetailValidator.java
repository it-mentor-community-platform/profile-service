package com.itmentorcommunityplatform.profileservice.validator;

import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;

/**
 * <p> Интерфейс для реализации специфичных валидаторов
 * для отдельных типов параметров профиля.</p>
 * <p>
 * Каждый валидатор отвечает за строгую проверку одного конкретного
 * ProfileDetailType.
 * <p>
 * Как создать новый валидатор: <br>
 * 1. Добавить новый элемент в ProfileDetailType. <br>
 * 2. Создать класс, реализующий ProfileDetailValidator. <br>
 * 3. Вернуть тип через getSupportedProfileDetailType()
 * (обычно: ProfileDetailType.XYZ). <br>
 * 4. Реализовать проверку в validate(). <br>
 * 5. Отметить класс @Component, чтобы Spring автоматически зарегистрировал его.
 * </p>
 */
public interface ProfileDetailValidator {
    /**
     * Возвращает ProfileDetailType, для которого этот валидатор предназначен.
     */
    ProfileDetailType getSupportedProfileDetailType();

    /**
     * Выполнить валидацию специфичную для конкретного ProfileDetailType.
     */
    void validate(String value);
}
