package com.finalsoft.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finalsoft.common.domain.persistence.AuditLog;

public interface AuditRepository extends JpaRepository<AuditLog, String> {
}
