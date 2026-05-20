package ru.ystu.analysis.input.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ystu.analysis.input.dto.analysis.AnalysisTaskDto;
import ru.ystu.analysis.input.mapper.AnalysisMapper;
import ru.ystu.analysis.input.model.MarketingData;
import ru.ystu.analysis.input.model.output.AnalysisResults;
import ru.ystu.analysis.input.model.output.RequestType;
import ru.ystu.analysis.input.model.output.Status;
import ru.ystu.analysis.input.repository.AnalysisResultsRepository;
import ru.ystu.analysis.input.repository.MarketingDataRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MarketingDataRepository marketingDataRepository;
    private final AnalysisResultsRepository externalRepository;
    private final AnalysisMapper analysisMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.marketing}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.analysis}")
    private String routingKey;

    @Transactional
    public UUID runFullAnalysis(Long userId) {
        UUID requestId = UUID.randomUUID();

        AnalysisResults placeholder = new AnalysisResults();
        placeholder.setRequestId(requestId);
        placeholder.setUserId(userId);
        placeholder.setStatus(Status.PROCESSING);
        placeholder.setRequestType(RequestType.ANALYSIS);
        placeholder.setCreatedAt(Instant.now());

        externalRepository.save(placeholder);

        List<MarketingData> userHistory = marketingDataRepository.findAllByUserId(userId);
        AnalysisTaskDto task = analysisMapper.toAnalysisTaskDto(userHistory, requestId, userId);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, task);

        return requestId;
    }
}
