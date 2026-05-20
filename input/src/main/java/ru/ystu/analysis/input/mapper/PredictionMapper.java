package ru.ystu.analysis.input.mapper;

import org.springframework.stereotype.Component;
import ru.ystu.analysis.input.dto.prediction.request.PredictionRequestDto;
import ru.ystu.analysis.input.dto.prediction.response.PredictionTaskDto;
import ru.ystu.analysis.input.model.Platform;
import ru.ystu.analysis.input.repository.PlatformRepository;

import java.util.UUID;

@Component
public class PredictionMapper {

    private final PlatformRepository platformRepository;

    public PredictionMapper(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public PredictionTaskDto toPredictionTaskDto(PredictionRequestDto dto, UUID requestId) {
        String platformName = platformRepository.findById(dto.platformId())
                .map(Platform::getPlatformName)
                .orElseThrow(() -> new RuntimeException("Platform not found with id " + dto.platformId()));

        return new PredictionTaskDto(
                requestId,
                Action.PREDICT.name(),
                platformName,
                dto.costs()
        );
    }

}
