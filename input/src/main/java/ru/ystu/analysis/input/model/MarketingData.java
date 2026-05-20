package ru.ystu.analysis.input.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "marketing_data", schema = "input_stage")
@Data
public class MarketingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "reporting_month", nullable = false)
    private LocalDate reportingMonth;

    @Column(name = "costs", nullable = false)
    private BigDecimal costs;

    @Column(name = "sales_volume", nullable = false)
    private BigDecimal salesVolume;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
