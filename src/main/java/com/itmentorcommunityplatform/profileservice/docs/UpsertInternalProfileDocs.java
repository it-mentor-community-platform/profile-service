package com.itmentorcommunityplatform.profileservice.docs;

import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
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
        summary = "Upsert user profile (internal)",
        description = """
                Create or update a user's profile details.
                It is used by internal services.
                Requires `telegram_user_id` and any set of details.
                
                ### Example request body
                ```json
                {
                  "telegram_user_id": 12345,
                  "details": {
                    "github_profile_url": "https://github.com/johndoe",
                    "telegram_url": "https://t.me/johndoe",
                    "first_name": "Arc",
                    "last_name": "Warden"
                  }
                }
                ```
                """,
        requestBody = @RequestBody(
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProfileUpsertInternalRequestDto.class),
                        examples = @ExampleObject(
                                name = "Internal profile upsert example",
                                value = """
                                        {
                                          "telegram_user_id": 12345,
                                          "details": {
                                            "github_profile_url": "https://github.com/johndoe",
                                            "telegram_url": "https://t.me/johndoe",
                                            "first_name": "Arc",
                                            "last_name": "Warden"
                                          }
                                        }
                                        """
                        )
                )
        )
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Profile created successfully"
        ),
        @ApiResponse(
                responseCode = "200",
                description = "Profile updated successfully"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = @Content(schema = @Schema(
                        example = "{\"message\":\"Bad request\"}"
                ))
        )
})
public @interface UpsertInternalProfileDocs {
}
