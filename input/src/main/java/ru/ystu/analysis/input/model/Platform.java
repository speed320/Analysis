package ru.ystu.analysis.input.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "platform", schema = "input_stage")
@Data
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "platform_name", nullable = false, unique = true)
    private String platformName;
}
