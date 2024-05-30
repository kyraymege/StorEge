package com.kyraymege.StorEge.controllers;

import com.kyraymege.StorEge.business.abstracts.DocumentService;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.entity.dto.UserDto;
import com.kyraymege.StorEge.entity.dto.dtoRequest.UpdateDocumentRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.kyraymege.StorEge.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = {"/documents"})
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('document:create') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> saveDocument(@AuthenticationPrincipal UserDto userDto, @RequestParam("files") List<MultipartFile> documents, HttpServletRequest request) {
        var docs = documentService.saveDocument(userDto.getUserId(), documents);
        return ResponseEntity.created(URI.create("")).body(getResponse(request, Map.of("documents", docs), "Documents uploaded successfully", HttpStatus.CREATED));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal UserDto userDto,
                                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 HttpServletRequest request) {
        var docs = documentService.getDocuments(page, size);
        return ResponseEntity.ok(getResponse(request, Map.of("documents", docs), "Documents retrieved successfully", HttpStatus.CREATED));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getSearchedDocuments(@AuthenticationPrincipal UserDto userDto,
                                                         @RequestParam(value = "size", defaultValue = "5") int size,
                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "query", defaultValue = "0") String query,
                                                         HttpServletRequest request) {
        var docs = documentService.getDocuments(page, size, query);
        return ResponseEntity.ok(getResponse(request, Map.of("documents", docs), "Searched documents retrieved successfully", HttpStatus.CREATED));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getDocument(@AuthenticationPrincipal UserDto userDto, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        var doc = documentService.getDocumentByDocumentId(documentId);
        return ResponseEntity.ok(getResponse(request, Map.of("document", doc), "Document retrieved successfully", HttpStatus.CREATED));
    }

    @PatchMapping
    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> updateDocument(@AuthenticationPrincipal UserDto userDto, @RequestBody UpdateDocumentRequest updateDocumentRequest, HttpServletRequest request) {
        var doc = documentService.updateDocument(updateDocumentRequest.getDocumentId(), updateDocumentRequest.getName(), updateDocumentRequest.getDescription());
        return ResponseEntity.ok(getResponse(request, Map.of("document", doc), "Document retrieved successfully", HttpStatus.CREATED));
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAnyAuthority('document:delete') or hasAnyRole('ADMIN')")
    public ResponseEntity<Response> deleteDocument(@AuthenticationPrincipal UserDto userDto, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Document deleted successfully", HttpStatus.CREATED));
    }

    @GetMapping("/download/{documentName}")
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@AuthenticationPrincipal UserDto userDto,  @PathVariable("documentName") String documentName) throws IOException {
        var resource = documentService.getResource(documentName);
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", documentName);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;File-Name=%s", resource.getFilename()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
                .headers(httpHeaders).body(resource);
    }
}
