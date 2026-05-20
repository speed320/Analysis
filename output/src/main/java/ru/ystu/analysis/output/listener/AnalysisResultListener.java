package ru.ystu.analysis.output.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.ystu.analysis.output.dto.analysis.request.PythonAnalysisRequestDto;
import ru.ystu.analysis.output.dto.predict.request.PredictionRequestDto;
import ru.ystu.analysis.output.mapper.AnalysisResultMapper;
import ru.ystu.analysis.output.model.AnalysisResults;
import ru.ystu.analysis.output.model.RequestType;
import ru.ystu.analysis.output.repository.AnalysisResultsRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisResultListener {

    private final ObjectMapper objectMapper;
    private final AnalysisResultsRepository repository;
    private final AnalysisResultMapper mapper;

    @RabbitListener(queues = "${rabbitmq.queue.results}")
    public void onMessage(JsonNode root) throws JsonProcessingException {
        log.info("Получено сообщение от Python: {}", root.get("action").asText());

        String action = root.get("action").asText();
        UUID requestId = UUID.fromString(root.get("request_id").asText());

        AnalysisResults entity = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (RequestType.ANALYSIS.name().equals(action)) {
            PythonAnalysisRequestDto response = objectMapper.treeToValue(root, PythonAnalysisRequestDto.class);
            mapper.mapAnalysisResponse(entity, response);
            log.info("Успешно обработан результат АНАЛИЗА для ID: {}", requestId);
        } else if (RequestType.PREDICTION.name().equals(action)) {
            PredictionRequestDto response = objectMapper.treeToValue(root, PredictionRequestDto.class);
            mapper.mapPredictionResponse(entity, response);
            log.info("Успешно обработан результат ПРЕДСКАЗАНИЯ для ID: {}", requestId);
        } else if(RequestType.ERROR.name().equals(action)) {
            PythonAnalysisRequestDto response = objectMapper.treeToValue(root, PythonAnalysisRequestDto.class);
            mapper.mapAnalysisResponse(entity, response);
            log.warn("Ошибка в python: {}", action);
        } else {
            log.warn("Получен неизвестный тип действия: {}", action);
        }

        repository.save(entity);
    }
}
