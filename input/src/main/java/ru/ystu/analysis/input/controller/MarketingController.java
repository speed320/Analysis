package ru.ystu.analysis.input.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ystu.analysis.input.dto.data.request.MarketingDataRequestDto;
import ru.ystu.analysis.input.dto.marketing.MarketingDataResponseDto;
import ru.ystu.analysis.input.dto.marketing.PlatformResponseDto;
import ru.ystu.analysis.input.dto.prediction.request.PredictionRequestDto;
import ru.ystu.analysis.input.service.AnalysisService;
import ru.ystu.analysis.input.service.MarketingService;
import ru.ystu.analysis.input.service.PredictionService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingService marketingService;
    private final AnalysisService analysisService;
    private final PredictionService predictionService;

    @PostMapping("/data")
    public ResponseEntity<String> addMarketingData(
            @RequestBody List<MarketingDataRequestDto> dto,
            Authentication authentication
            ){
        Long userId = extractUserId(authentication);
        marketingService.saveAndProcess(dto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body("Данные успешно сохранены и отправлены на обучение");
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, UUID>> runAnalysis(Authentication authentication) {
        Long userId = extractUserId(authentication);
        UUID requestId = analysisService.runFullAnalysis(userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("requestId", requestId));
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, UUID>> predictSales(
            @RequestBody PredictionRequestDto dto,
            Authentication authentication
            ) {
        Long userId = extractUserId(authentication);
        UUID requestId = predictionService.initiatePrediction(dto, userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("requestId", requestId));
    }

    @GetMapping("/all/data")
    public ResponseEntity<List<MarketingDataResponseDto>> getAllData(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<MarketingDataResponseDto> data = marketingService.getAllRecords(userId);

        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/all/data/{id}")
    public ResponseEntity<Void> deleteData(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        marketingService.delete(id, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformResponseDto>> getPlatforms() {
        List<PlatformResponseDto> platforms = marketingService.getAllPlatforms();
        return ResponseEntity.ok(platforms);
    }

    private Long extractUserId(Authentication authentication){
        return Long.valueOf(authentication.getName());
    }
}
