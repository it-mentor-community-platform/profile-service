package com.itmentorcommunityplatform.profileservice.docs;

import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Get profile by GitHub URL (internal)",
        description = "Returns the user profile from a GitHub URL"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile received successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProfileDto.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Profile not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\"message\":\"Profile with URL: https://github.com/popa123 not found\"}")
                ))
}
)
public @interface GetProfileByGithubUrl {
}
