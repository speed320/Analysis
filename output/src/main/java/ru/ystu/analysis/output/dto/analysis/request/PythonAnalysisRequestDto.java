package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PythonAnalysisRequestDto(
        @JsonProperty("request_id")
        UUID requestId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("action")
        String action,

        @JsonProperty("status")
        String status,

        @JsonProperty("data")
        AnalysisDataDto data
) {
}
