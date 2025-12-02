package com.itmentorcommunityplatform.profileservice.validator;

/**
 * <p> Интерфейс для реализации специфичных валидаторов
 * для отдельных типов параметров профиля.</p>
 *
 * Каждый валидатор отвечает за строгую проверку одного конкретного
 * ProfileDetailType.
 * <p>
 * Как создать новый валидатор: <br>
 *  1. Добавить новый элемент в ProfileDetailType. <br>
 *  2. Создать класс, реализующий ProfileDetailValidator. <br>
 *  3. Вернуть тип через getProfileDetailTypeName()
 *     (обычно: ProfileDetailType.XYZ.getDetailName()). <br>
 *  4. Реализовать проверку в validate(). <br>
 *  5. Отметить класс @Component, чтобы Spring автоматически зарегистрировал его.
 *  </p>
 */
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
