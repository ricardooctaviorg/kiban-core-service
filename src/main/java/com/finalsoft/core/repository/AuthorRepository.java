package com.finalsoft.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finalsoft.common.domain.persistence.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
