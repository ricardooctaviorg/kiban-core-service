package com.finalsoft.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finalsoft.common.domain.persistence.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
