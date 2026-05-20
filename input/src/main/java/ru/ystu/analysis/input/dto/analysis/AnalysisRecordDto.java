package ru.ystu.analysis.input.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AnalysisRecordDto(
        @JsonProperty
        String platform,

        @JsonProperty("month")
        String month,

        @JsonProperty("costs")
        BigDecimal costs,

        @JsonProperty("sales")
        BigDecimal sales
) {
}
