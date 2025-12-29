package com.itmentorcommunityplatform.profileservice.docs;


import com.itmentorcommunityplatform.profileservice.dto.ProfileResponseDto;
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
        summary = "Get user profile by id ",
        description = "Returns the user profile from a id"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile received successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProfileResponseDto.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Profile not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\"message\":\"Profile with id: 228 not found\"}")
                ))
}
)
public @interface GetUserProfileByIdDocs {
}
