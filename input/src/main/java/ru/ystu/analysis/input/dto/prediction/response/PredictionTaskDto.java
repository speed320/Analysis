package ru.ystu.analysis.input.dto.prediction.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PredictionTaskDto(
        @JsonProperty("request_id")
        UUID requestId,

        @JsonProperty("action")
        String action,

        @JsonProperty("platform")
        String platformName,

        @JsonProperty("planned_costs")
        BigDecimal costs
) {
}
