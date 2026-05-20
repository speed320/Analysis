package ru.ystu.analysis.input.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TrainRecordDto(
        @JsonProperty("platform")
        String platformName,

        @JsonProperty("costs")
        BigDecimal costs,

        @JsonProperty("sales")
        BigDecimal sales
) {
}
