package com.itmentorcommunityplatform.profileservice.docs;

import com.itmentorcommunityplatform.profileservice.dto.response.AchievementResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
        summary = "Update achievement visibility",
        description = "Allows a user to change the visibility (public/private) of a specific achievement by its type"
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Achievement visibility successfully updated",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AchievementResponseDto.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "type": "SPRINTER",
                                          "earned_timestamp": 1766490639328,
                                          "description": "Сданы с 1 по 6 проект за 6 месяцев!",
                                          "publicly_visible": true
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request data or unknown achievement type",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Invalid achievement type: BETA_TESTER123123"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "User does not own the achievement",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "User 1 does not own the achievement they are trying to edit"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Profile not found",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Profile with Telegram-User-Id 1 does not exist"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Server error or invalid header format",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Unknown error: Method parameter 'X-Telegram-User-Id': Failed to convert value..."
                                        }
                                        """
                        )
                )
        )
})
public @interface SetAchievementVisibilityDocs {
}