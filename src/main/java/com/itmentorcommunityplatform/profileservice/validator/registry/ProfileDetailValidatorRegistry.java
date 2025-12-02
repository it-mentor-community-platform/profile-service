package com.itmentorcommunityplatform.profileservice.validator.registry;

import com.itmentorcommunityplatform.profileservice.validator.ProfileDetailValidator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ProfileDetailValidatorRegistry {
    private final Map<String, ProfileDetailValidator> validatorsMap = new HashMap<>();

    public ProfileDetailValidatorRegistry(List<ProfileDetailValidator> validators) {
        for (ProfileDetailValidator v : validators) {
            validatorsMap.put(v.getProfileDetailTypeName(), v);
        }
    }

    public Optional<ProfileDetailValidator> getSpecificValidator(String detailName) {
        return Optional.ofNullable(validatorsMap.get(detailName));
    }
}
