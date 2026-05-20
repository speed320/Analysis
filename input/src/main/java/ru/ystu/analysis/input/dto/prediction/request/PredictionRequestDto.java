package ru.ystu.analysis.input.dto.prediction.request;

import java.math.BigDecimal;

public record PredictionRequestDto(
        Long platformId,
        BigDecimal costs
) {
}
