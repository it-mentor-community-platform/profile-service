package com.itmentorcommunityplatform.profileservice.validator.impl;

import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.validator.ProfileDetailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

import static com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType.GITHUB_PROFILE_URL;

@Component
public class GithubProfileUrlValidator implements ProfileDetailValidator {

    private static final Pattern GITHUB_PATTERN = Pattern.compile("^https://github\\.com/[^\\s]+$");

    @Override
    public ProfileDetailType getSupportedProfileDetailType() {
        return GITHUB_PROFILE_URL;
    }

    @Override
    public void validate(String value) {
        if (value == null || !GITHUB_PATTERN.matcher(value).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Github profile url incorrect");
        }
    }
}
