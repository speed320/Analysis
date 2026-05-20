package ru.ystu.analysis.input.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ystu.analysis.input.model.MarketingData;

import java.util.List;

public interface MarketingDataRepository extends JpaRepository<MarketingData, Long> {
    List<MarketingData> findAllByUserId(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}