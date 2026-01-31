package com.itmentorcommunityplatform.profileservice.docs;

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
        summary = "Get profile by Telegram URL (internal)",
        description = "Returns the user profile from a Telegram URL"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile received successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = """
                                        {
                                          "telegram_user_id": 12345,
                                          "details": {
                                            "github_profile_url": "https://github.com/zhukov",
                                            "telegram_url": "https://t.me/zhukov",
                                            "first_name": "Sergey",
                                            "last_name": "Zhukov"
                                          }
                                        }
                                """)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error (invalid Telegram URL)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = """
                                        {
                                          "message": "Telegram profile url incorrect"
                                        }
                                """)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Profile not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = """
                                        {
                                          "message": "Profile with given Telegram URL not found"
                                        }
                                """)
                )
        )
})
public @interface GetProfileByTgUrlDocs {
}
