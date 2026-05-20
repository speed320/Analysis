package ru.ystu.analysis.output.dto.analysis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record VisualizationDataDto(
        @JsonProperty("scatter_plot")
        List<List<BigDecimal>> scatterPlot,

        @JsonProperty("time_series_costs")
        List<List<Object>> timeSeriesCosts,

        @JsonProperty("time_series_sales")
        List<List<Object>> timeSeriesSales
) {
}
