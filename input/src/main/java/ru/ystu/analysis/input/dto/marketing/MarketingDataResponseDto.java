package ru.ystu.analysis.input.dto.marketing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketingDataResponseDto(
        Long id,
        String platformName,
        LocalDate reportingMonth,
        BigDecimal costs,
        BigDecimal salesVolume
) {
}
