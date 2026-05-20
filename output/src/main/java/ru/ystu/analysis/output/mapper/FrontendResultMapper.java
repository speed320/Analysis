package ru.ystu.analysis.output.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ystu.analysis.output.dto.analysis.response.AnalysisResultResponseDto;
import ru.ystu.analysis.output.model.AnalysisResults;

@Component
@RequiredArgsConstructor
public class FrontendResultMapper {

    private final ObjectMapper objectMapper;

    public AnalysisResultResponseDto toDto(AnalysisResults entity) {
        JsonNode parsedData = null;

        if (entity.getResultData() != null) {
            try {
                parsedData = objectMapper.readTree(entity.getResultData());
            } catch (JsonProcessingException e) {
                parsedData = null;
            }
        }

        return new AnalysisResultResponseDto(
                entity.getStatus(),
                entity.getRequestType(),
                parsedData
        );
    }
}
