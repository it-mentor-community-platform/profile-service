package com.itmentorcommunityplatform.profileservice.validator.base;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * <p>Базовый валидатор.</p>
 * <p>
 * Основное назначение: <br>
 * - выполнение общих для всех detailName проверок
 * (например, ограничение максимальной длины строки).<br>
 * Если требуется добавить общие проверки для всех details,
 * их следует разместить здесь.
 */
@Component
public class BaseProfileDetailValidator {

    public void validate(String detailName, String value) {
        if (value == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Value of '" + detailName + "' cannot be null");
        } else if (value.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Value of '" + detailName + "' cannot be empty or whitespace only");
        } else if (value.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Value of '" + detailName + "' exceeds max length 255");
        }

    }
}
