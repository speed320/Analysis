package ru.ystu.analysis.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ystu.analysis.output.model.AnalysisResults;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisResultsRepository extends JpaRepository<AnalysisResults, UUID> {
    Optional<AnalysisResults> findByRequestIdAndUserId(UUID requestId, Long userId);
}
