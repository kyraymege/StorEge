package com.kyraymege.StorEge.repositories;

import com.kyraymege.StorEge.entity.concretes.Document;
import com.kyraymege.StorEge.entity.dto.IDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.kyraymege.StorEge.utils.consts.Constants.*;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(countQuery = "SELECT COUNT(*) FROM documents", value = SELECT_DOCUMENTS_QUERY, nativeQuery = true)
    Page<IDocument> findDocuments(Pageable pageable);

    @Query(countQuery = "SELECT COUNT(*) FROM documents WHERE name ~* :documentName", value = SELECT_DOCUMENTS_BYNAME_QUERY, nativeQuery = true)
    Page<IDocument> findDocumentsByName(@Param("documentName") String documentName, Pageable pageable);

    @Query(value = SELECT_DOCUMENT_QUERY, nativeQuery = true)
    Optional<IDocument> findDocumentByDocumentId(String documentId);

    Optional<Document> findByDocumentId(String documentId);
}
