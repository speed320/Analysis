package ru.ystu.analysis.input.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ystu.analysis.input.model.Platform;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
}
