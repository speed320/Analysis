package ru.ystu.analysis.input.dto.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TrainingTaskDto(
        @JsonProperty("action")
        String action,

        @JsonProperty("data")
        List<TrainRecordDto> data
) {
}
