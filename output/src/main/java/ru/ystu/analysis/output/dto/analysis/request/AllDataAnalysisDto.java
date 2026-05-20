package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllDataAnalysisDto(
        @JsonProperty("scope")
        String scope,

        @JsonProperty("correlation_coefficient")
        Double correlationCoefficient,

        @JsonProperty("visualization_data")
        VisualizationDataDto visualizationData
) {
}
