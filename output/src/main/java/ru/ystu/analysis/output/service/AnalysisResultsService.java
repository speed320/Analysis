package ru.ystu.analysis.output.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ystu.analysis.output.dto.analysis.response.AnalysisResultResponseDto;
import ru.ystu.analysis.output.mapper.FrontendResultMapper;
import ru.ystu.analysis.output.repository.AnalysisResultsRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisResultsService {
    private final AnalysisResultsRepository repository;
    private final FrontendResultMapper frontendMapper;

    @Transactional(readOnly = true)
    public Optional<AnalysisResultResponseDto> getResult(UUID requestId, Long userId) {
        return repository.findByRequestIdAndUserId(requestId, userId)
                .map(frontendMapper::toDto);
    }
}
