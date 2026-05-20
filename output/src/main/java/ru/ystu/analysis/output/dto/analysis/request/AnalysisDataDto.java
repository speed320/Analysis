package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnalysisDataDto(
        @JsonProperty("platforms_analysis")
        List<PlatformAnalysisDto> platformsAnalysis,

        @JsonProperty("all_data_analysis")
        AllDataAnalysisDto allDataAnalysis
) {
}
