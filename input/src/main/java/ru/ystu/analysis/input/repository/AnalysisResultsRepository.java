package ru.ystu.analysis.input.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ystu.analysis.input.model.output.AnalysisResults;

@Repository
public interface AnalysisResultsRepository extends JpaRepository<AnalysisResults, Long> {
}
