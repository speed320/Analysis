package ru.ystu.analysis.output.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ystu.analysis.output.dto.analysis.response.AnalysisResultResponseDto;
import ru.ystu.analysis.output.service.AnalysisResultsService;

import java.util.UUID;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class AnalysisResultsController {

    private final AnalysisResultsService analysisResultsService;

    @GetMapping("/{requestId}")
    public ResponseEntity<AnalysisResultResponseDto> getResult(
            @PathVariable UUID requestId,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return analysisResultsService.getResult(requestId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
