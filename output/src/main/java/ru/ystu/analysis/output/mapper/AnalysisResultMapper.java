package ru.ystu.analysis.output.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ystu.analysis.output.dto.analysis.request.PythonAnalysisRequestDto;
import ru.ystu.analysis.output.dto.predict.request.PredictionRequestDto;
import ru.ystu.analysis.output.model.AnalysisResults;
import ru.ystu.analysis.output.model.Status;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AnalysisResultMapper {

    private final ObjectMapper objectMapper;

    public void mapAnalysisResponse(AnalysisResults entity, PythonAnalysisRequestDto response) {
        entity.setStatus(Status.COMPLETED);
        entity.setCompletedAt(Instant.now());

        try {
            String jsonResult = objectMapper.writeValueAsString(response.data());
            entity.setResultData(jsonResult);
        } catch (JsonProcessingException e) {
            entity.setStatus(Status.ERROR);
        }
    }

    public void mapPredictionResponse(AnalysisResults entity, PredictionRequestDto response) {
        entity.setStatus(Status.COMPLETED);
        entity.setCompletedAt(Instant.now());

        try {
            var resultContent = java.util.Map.of(
                    "predicted_sales", response.predictedSales(),
                    "platform", response.platform(),
                    "planned_costs", response.plannedCosts()
            );

            String jsonResult = objectMapper.writeValueAsString(resultContent);
            entity.setResultData(jsonResult);

        } catch (JsonProcessingException e) {
            entity.setStatus(Status.ERROR);
        }
    }
}
