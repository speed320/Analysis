package ru.ystu.analysis.input.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ystu.analysis.input.dto.data.request.MarketingDataRequestDto;
import ru.ystu.analysis.input.dto.data.response.TrainingTaskDto;
import ru.ystu.analysis.input.dto.marketing.MarketingDataResponseDto;
import ru.ystu.analysis.input.dto.marketing.PlatformResponseDto;
import ru.ystu.analysis.input.mapper.MarketingDataMapper;
import ru.ystu.analysis.input.model.MarketingData;
import ru.ystu.analysis.input.repository.MarketingDataRepository;
import ru.ystu.analysis.input.repository.PlatformRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketingService {

    private final MarketingDataRepository marketingDataRepository;
    private final MarketingDataMapper marketingMapper;
    private final RabbitTemplate rabbitTemplate;
    private final PlatformRepository platformRepository;

    @Value("${rabbitmq.exchange.marketing}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.train}")
    private String routingKey;

    @Transactional
    public void saveAndProcess(List<MarketingDataRequestDto> dto, Long userId) {
        List<MarketingData> entities = marketingMapper.toEntityList(dto, userId);
        marketingDataRepository.saveAll(entities);

        TrainingTaskDto trainingTask = marketingMapper.toTrainingTaskDto(dto);

        rabbitTemplate.convertAndSend(exchangeName, routingKey, trainingTask);
    }

    public List<MarketingDataResponseDto> getAllRecords(Long userId) {
        return marketingDataRepository.findAllByUserId(userId).stream()
                .map(marketingMapper::toResponseDto).toList();
    }

    @Transactional
    public void delete(Long id,Long userId) {
        marketingDataRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional
    public List<PlatformResponseDto> getAllPlatforms() {
        return platformRepository.findAll().stream()
                .map(marketingMapper::toPlatformDto).toList();
    }


}
