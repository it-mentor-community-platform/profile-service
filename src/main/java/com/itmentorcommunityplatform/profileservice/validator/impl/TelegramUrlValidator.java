package com.itmentorcommunityplatform.profileservice.validator.impl;

import com.itmentorcommunityplatform.profileservice.validator.ProfileDetailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

import static com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType.TELEGRAM_URL;

@Component
public class TelegramUrlValidator implements ProfileDetailValidator {

    private static final Pattern TELEGRAM_PATTERN = Pattern.compile("^https://t\\.me/[^\\s/]+$");

    @Override
    public String getProfileDetailTypeName() {
        return TELEGRAM_URL.getDetailName();
    }

    @Override
    public void validate(String value) {
        if (value == null || !TELEGRAM_PATTERN.matcher(value).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telegram profile url incorrect");
        }
    }
}
