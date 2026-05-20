package ru.ystu.analysis.input.mapper;

import org.springframework.stereotype.Component;
import ru.ystu.analysis.input.dto.data.request.MarketingDataRequestDto;
import ru.ystu.analysis.input.dto.data.response.TrainRecordDto;
import ru.ystu.analysis.input.dto.data.response.TrainingTaskDto;
import ru.ystu.analysis.input.dto.marketing.MarketingDataResponseDto;
import ru.ystu.analysis.input.dto.marketing.PlatformResponseDto;
import ru.ystu.analysis.input.model.MarketingData;
import ru.ystu.analysis.input.model.Platform;
import ru.ystu.analysis.input.repository.PlatformRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MarketingDataMapper {

    private final PlatformRepository platformRepository;

    public MarketingDataMapper(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public TrainingTaskDto toTrainingTaskDto(List<MarketingDataRequestDto> incomingData) {
        Map<Long, String> platformNames = platformRepository.findAll().stream().
                collect(Collectors.toMap(Platform::getId, Platform::getPlatformName));

        List<TrainRecordDto> records = incomingData.stream()
                .map(dto -> new TrainRecordDto(
                        platformNames.getOrDefault(dto.platformId(), "Unknown"),
                        dto.costs(),
                        dto.salesVolume()
                )).toList();

        return new TrainingTaskDto(Action.TRAIN.name(), records);
    }

    public List<MarketingData> toEntityList(List<MarketingDataRequestDto> dtos, Long userId) {
        return dtos.stream()
                .map(dto -> toEntity(dto, userId))
                .toList();
    }

    public MarketingData toEntity(MarketingDataRequestDto dto, Long userId) {
        MarketingData entity = new MarketingData();

        entity.setUserId(userId);
        entity.setReportingMonth(dto.reportingMonth());
        entity.setCosts(dto.costs());
        entity.setSalesVolume(dto.salesVolume());

        Platform platform = platformRepository.getReferenceById(dto.platformId());
        entity.setPlatform(platform);

        return entity;
    }

    public MarketingDataResponseDto toResponseDto(MarketingData entity) {
        return new MarketingDataResponseDto(
                entity.getId(),
                entity.getPlatform().getPlatformName(),
                entity.getReportingMonth(),
                entity.getCosts(),
                entity.getSalesVolume()
        );
    }

    public PlatformResponseDto toPlatformDto(Platform entity) {
        return new PlatformResponseDto(
                entity.getId(),
                entity.getPlatformName()
        );
    }
}
