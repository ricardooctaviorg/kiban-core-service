package com.finalsoft.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finalsoft.common.domain.persistence.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
