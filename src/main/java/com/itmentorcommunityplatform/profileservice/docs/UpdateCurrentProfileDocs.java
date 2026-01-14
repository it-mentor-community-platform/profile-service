package com.itmentorcommunityplatform.profileservice.docs;


import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileDetailsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Update profile of current user",
        description = """
                Update current user's profile.
                Requires header `X-Telegram-User-Id`.
                
                ### Example request body
                ```json
                {
                  "github_profile_url": "https://github.com/johndoe",
                  "first_name": "Dmitry",
                  "last_name": "OxErr"
                }
                ```
                """,
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProfileUpdateRequestDto.class),
                        examples = @ExampleObject(
                                name = "Profile update example",
                                value = """
                                        {
                                          "github_profile_url": "https://github.com/johndoe",
                                          "first_name": "Dmitry",
                                          "last_name": "OxErr"
                                        }
                                        """
                        )
                )
        )
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile updated successfully. Returns full profile state.",
                content = @Content(schema = @Schema(implementation = ProfileDetailsResponseDto.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error (invalid fields, incorrect GitHub URL format, or empty body)",
                content = @Content(schema = @Schema(example = "{\"message\":\"Invalid request or validation failed\"}"))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Attempt to modify protected field 'telegram_url'",
                content = @Content(schema = @Schema(example = "{\"message\":\"Modifying 'telegram_url' is not allowed\"}"))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Profile for the current user does not exist",
                content = @Content(schema = @Schema(example = "{\"message\":\"Profile not found\"}"))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Unknown internal server error",
                content = @Content(schema = @Schema(example = "{\"message\":\"An unexpected error occurred\"}"))
        )
})
public @interface UpdateCurrentProfileDocs {
}
