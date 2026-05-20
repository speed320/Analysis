package ru.ystu.analysis.input.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record AnalysisTaskDto(
        @JsonProperty("request_id")
        UUID requestId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("action")
        String action,

        @JsonProperty("data")
        List<AnalysisRecordDto> data
) {
}
