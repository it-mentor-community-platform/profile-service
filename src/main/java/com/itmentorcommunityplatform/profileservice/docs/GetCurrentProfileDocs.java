package com.itmentorcommunityplatform.profileservice.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
        summary = "Get current user's profile",
        description = "Retrieve the profile of the current user.\n" +
                "Requires header `X-Telegram-User-Id`.",
        parameters = {
                @Parameter(
                        name = "X-Telegram-User-Id",
                        description = "Telegram user ID",
                        required = true,
                        in = ParameterIn.HEADER,
                        example = "628570951"
                )
        }
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Profile retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = """
                                {
                                  "details": {
                                    "github_profile_url": "https://github.com/zhukov",
                                    "telegram_url": "https://t.me/zhukov",
                                    "last_name": "Zhukov",
                                    "first_name": "Sergey"
                                  },
                                  "achievements": [
                                    {
                                      "type": "SPRINTER",
                                      "earned_timestamp": 711213212
                                    }
                                  ]
                                }
                                """)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request",
                content = @Content(schema = @Schema(
                        example = "{\"message\":\"Bad request\"}"
                ))
        )
})
public @interface GetCurrentProfileDocs {
}
