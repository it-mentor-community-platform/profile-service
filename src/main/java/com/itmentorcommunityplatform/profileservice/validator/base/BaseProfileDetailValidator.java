package com.itmentorcommunityplatform.profileservice.validator.base;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BaseProfileDetailValidator {

    public void validate(String detailName, String value) {
        if (value != null && value.length() > 255)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Value of '" + detailName + "' exceeds max length 255");
    }
}
