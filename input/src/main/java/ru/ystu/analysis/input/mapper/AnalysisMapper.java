package ru.ystu.analysis.input.mapper;

import org.springframework.stereotype.Component;
import ru.ystu.analysis.input.dto.analysis.AnalysisRecordDto;
import ru.ystu.analysis.input.dto.analysis.AnalysisTaskDto;
import ru.ystu.analysis.input.model.MarketingData;

import java.util.List;
import java.util.UUID;

@Component
public class AnalysisMapper {

    public AnalysisTaskDto toAnalysisTaskDto(List<MarketingData> entities, UUID requestId, Long userId) {
        List<AnalysisRecordDto> records = entities.stream()
                .map(entity -> new AnalysisRecordDto(
                        entity.getPlatform().getPlatformName(),
                        entity.getReportingMonth().toString(),
                        entity.getCosts(),
                        entity.getSalesVolume()
                )).toList();

        return new AnalysisTaskDto(requestId, userId, Action.FULL_ANALYSIS.name(), records);
    }
}
