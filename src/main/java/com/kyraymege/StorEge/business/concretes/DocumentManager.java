package com.kyraymege.StorEge.business.concretes;

import com.kyraymege.StorEge.business.abstracts.DocumentService;
import com.kyraymege.StorEge.business.abstracts.UserService;
import com.kyraymege.StorEge.entity.concretes.Document;
import com.kyraymege.StorEge.entity.dto.DocumentDto;
import com.kyraymege.StorEge.entity.dto.IDocument;
import com.kyraymege.StorEge.exceptions.APIException;
import com.kyraymege.StorEge.repositories.DocumentRepository;
import com.kyraymege.StorEge.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.kyraymege.StorEge.utils.DocumentUtils.*;
import static com.kyraymege.StorEge.utils.consts.Constants.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DocumentManager implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return documentRepository.findDocumentsByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Collection<DocumentDto> saveDocument(String userId, List<MultipartFile> documents) {
        List<DocumentDto> newDocuments = new ArrayList<>();
        var user = userRepository.findUserByUserId(userId).orElseThrow(()-> new APIException("User not found"));
        var storage = Paths.get(FILE_DIRECTORY).toAbsolutePath();
        try {
            for (MultipartFile document : documents) {
                var fileName = cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
                if (fileName.contains("..")) {
                    throw new APIException("Invalid file name");
                }
                var doc = Document.builder()
                        .documentId(UUID.randomUUID().toString())
                        .name(fileName)
                        .description("No description")
                        .uri(getDocumentUri(fileName))
                        .formattedSize(byteCountToDisplaySize(document.getSize()))
                        .icon(setIcon(getExtension(fileName)))
                        .extension(getExtension(fileName))
                        .owner(user)
                        .build();
                var savedDocument = documentRepository.save(doc);
                Files.copy(document.getInputStream(), storage.resolve(fileName), REPLACE_EXISTING);
                DocumentDto newDocument = documentEntityToDto(savedDocument, userService.getUserById(savedDocument.getCreatedBy()), userService.getUserById(savedDocument.getUpdatedBy()));
                newDocuments.add(newDocument);
            }
            return newDocuments;
        } catch (Exception e) {
            throw new APIException("Error occurred while saving document");
        }
    }


    @Override
    public IDocument updateDocument(String documentId, String name, String description) {
        try {
            var document = getDocumentEntity(documentId);
            var doc = Paths.get(FILE_DIRECTORY).resolve(document.getName()).toAbsolutePath().normalize();
            Files.move(doc, doc.resolveSibling(name), REPLACE_EXISTING);
            document.setName(name);
            document.setDescription(description);
            documentRepository.save(document);
            return getDocumentByDocumentId(documentId);
        } catch (Exception e) {
            throw new APIException("Error occurred while updating document");
        }
    }

    private Document getDocumentEntity(String documentId) {
        return documentRepository.findByDocumentId(documentId).orElseThrow(() -> new APIException("Document not found"));
    }

    @Override
    public void deleteDocument(String documentId) {
        try {
            var document = getDocumentEntity(documentId);
            Files.deleteIfExists(Paths.get(FILE_DIRECTORY).resolve(document.getName()).toAbsolutePath().normalize());
            documentRepository.delete(document);
        } catch (Exception e) {
            throw new APIException("Error occurred while deleting document");
        }
    }

    @Override
    public IDocument getDocumentByDocumentId(String documentId) {
        return documentRepository.findDocumentByDocumentId(documentId).orElseThrow(() -> new APIException("Document not found"));
    }

    @Override
    public Resource getResource(String documentName) {
        try {
            var filePath = Paths.get(FILE_DIRECTORY).toAbsolutePath().normalize().resolve(documentName);
            if (!Files.exists(filePath)) {
                throw new APIException("Document not found");
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new APIException("Error occurred while getting document");
        }
    }
}
