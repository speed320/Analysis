package ru.ystu.analysis.output.dto.predict.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PredictionRequestDto(
        @JsonProperty("request_id")
        UUID requestId,

        @JsonProperty("action")
        String action,

        @JsonProperty("status")
        String status,

        @JsonProperty("predicted_sales")
        BigDecimal predictedSales,

        @JsonProperty("platform")
        String platform,

        @JsonProperty("planned_costs")
        BigDecimal plannedCosts
) {}
