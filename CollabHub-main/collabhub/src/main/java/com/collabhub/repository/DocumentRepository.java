package com.collabhub.repository;

import com.collabhub.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByProject_Id(Long projectId);
}
