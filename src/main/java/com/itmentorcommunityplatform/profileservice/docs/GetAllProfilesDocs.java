package com.itmentorcommunityplatform.profileservice.docs;

import com.itmentorcommunityplatform.profileservice.dto.response.AllProfilesPaginatedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        summary = "Get all profiles (admin)",
        description = "Returns all profiles with pagination",
        parameters = {
                @Parameter(
                        name = "page_size",
                        description = "Number of profiles per page",
                        required = true,
                        example = "10",
                        schema = @Schema(minimum = "1", type = "integer")
                ),

                @Parameter(
                        name = "page_number",
                        description = "Page number to return",
                        required = true,
                        example = "1",
                        schema = @Schema(minimum = "1", type = "integer")
                ),
                @Parameter(
                        name = "detailFilters",
                        description = "Filters in format detail_name=value.",
                        schema = @Schema(type = "object"),
                        examples = {
                                @ExampleObject(
                                        name = "Single filter",
                                        value = "{\"first_name\": \"Sergey\"}"
                                ),
                                @ExampleObject(
                                        name = "Multiple filters",
                                        value = "{\"first_name\": \"Sergey\", \"last_name\": \"Zhukov\"}"
                                )
                        }
                )
        }
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "All profiles",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AllProfilesPaginatedResponseDto.class)

                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid query parameters",
                content = @Content(
                        examples = {
                                @ExampleObject(
                                        name = "Invalid pagination parametr",
                                        summary = "page_size or page_number is invalid",
                                        description = "Example response when page_size or page_number is less than 1",
                                        value = "{\"message\":\"Validation failure\"}"
                                ),
                                @ExampleObject(
                                        name = "Invalid page number filters",
                                        summary = "page_number greater then total pages",
                                        description = "Example response when request page number is greater than total page count",
                                        value = "{\"message\":\"Page number is greater than total page count\"}"
                                ),
                                @ExampleObject(
                                        name = "Invalid details filters",
                                        summary = "details filters are invalid",
                                        description = "Example response when detail filter is unknown field",
                                        value = "{\"message\":\"Unknown detail name: unknown_field\"}"
                                )},
                        mediaType = "application/json"

                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "User dont have ADMIN role in header",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                example = "{\"message\":\"Access denied: missing ADMIN role in X-User-Roles header\"}"
                        )

                )
        )
}
)
public @interface GetAllProfilesDocs {
}
