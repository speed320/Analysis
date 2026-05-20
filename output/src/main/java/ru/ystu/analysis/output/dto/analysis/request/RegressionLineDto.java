package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegressionLineDto(
        @JsonProperty("slope")
        Double slope,

        @JsonProperty("intercept")
        Double intercept
) {
}
