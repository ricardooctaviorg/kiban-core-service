package com.finalsoft.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finalsoft.common.domain.persistence.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
