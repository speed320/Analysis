package ru.ystu.analysis.output.dto.analysis.response;

import com.fasterxml.jackson.databind.JsonNode;
import ru.ystu.analysis.output.model.RequestType;
import ru.ystu.analysis.output.model.Status;

public record AnalysisResultResponseDto(
        Status status,
        RequestType requestType,
        JsonNode resultData
) {}
