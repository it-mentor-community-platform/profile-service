package com.itmentorcommunityplatform.profileservice.docs;



import com.itmentorcommunityplatform.profileservice.dto.ProfileDetailDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
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
                  "telegram_url": "https://t.me/johndoe"
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
                                          "telegram_url": "https://t.me/johndoe"
                                        }
                                        """
                        )
                )
        )
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile updated successfully",
                content = @Content(schema = @Schema(implementation = ProfileDetailDto.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = @Content(schema = @Schema(
                        example = "{\"message\":\"Bad request\"}"
                ))
        )
})
public @interface UpdateCurrentProfileDocs {
}
