package com.itmentorcommunityplatform.profileservice.docs;

import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.ProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
        summary = "Get all achievements profile",
        description = "Returns all achievements that a profile may have"

)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "List achievements",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(
                                schema = @Schema(implementation = AchievementDto.class)
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "The required X-Telegram-User-Id header is missing.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\n" +
                                "  \"message\": \"The required title is missing: X-Telegram-User-Id\"\n" +
                                "}")
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Invalid X-Telegram-User-Id header format",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\n" +
                                "  \"message\": \"Unknown error: Method parameter 'X-Telegram-User-Id': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \\\"not-user\\\"\"\n" +
                                "}")
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description ="Profile not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\n" +
                                "  \"message\": \"Unknown error: Profile not found!\"\n" +
                                "}")
                )
        ),
        @ApiResponse(
                responseCode = "200",
                description = "User without achievements",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(
                                schema = @Schema(implementation = AchievementDto.class)
                        )
                )
        )
}
)

public @interface GetProfileAchievements {
}
