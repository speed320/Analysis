package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlatformAnalysisDto(
        @JsonProperty("platform")
        String platform,

        @JsonProperty("correlation_coefficient")
        Double correlationCoefficient,

        @JsonProperty("visualization_data")
        VisualizationDataDto visualizationData
) {
}
