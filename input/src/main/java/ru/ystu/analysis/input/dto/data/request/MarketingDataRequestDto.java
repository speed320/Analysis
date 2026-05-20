package ru.ystu.analysis.input.dto.data.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketingDataRequestDto(
        Long platformId,
        LocalDate reportingMonth,
        BigDecimal costs,
        BigDecimal salesVolume
){
}
