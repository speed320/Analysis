package ru.ystu.analysis.input.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ystu.analysis.input.dto.prediction.request.PredictionRequestDto;
import ru.ystu.analysis.input.dto.prediction.response.PredictionTaskDto;
import ru.ystu.analysis.input.mapper.PredictionMapper;
import ru.ystu.analysis.input.model.output.AnalysisResults;
import ru.ystu.analysis.input.model.output.RequestType;
import ru.ystu.analysis.input.model.output.Status;
import ru.ystu.analysis.input.repository.AnalysisResultsRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final AnalysisResultsRepository externalRepository;
    private final PredictionMapper predictionMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.marketing}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.predict}")
    private String routingKey;

    @Transactional
    public UUID initiatePrediction(PredictionRequestDto dto, Long userId) {
        UUID requestId = UUID.randomUUID();

        AnalysisResults placeholder = new AnalysisResults();
        placeholder.setRequestId(requestId);
        placeholder.setUserId(userId);
        placeholder.setStatus(Status.PROCESSING);
        placeholder.setRequestType(RequestType.PREDICTION);
        placeholder.setCreatedAt(Instant.now());

        externalRepository.save(placeholder);

        PredictionTaskDto task = predictionMapper.toPredictionTaskDto(dto, requestId);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, task);

        return requestId;
    }
}
