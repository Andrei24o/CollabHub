package com.collabhub.controller;


import com.collabhub.model.Document;
import com.collabhub.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Document> uploadDocument(@PathVariable Long projectId, @RequestParam("file") MultipartFile file, Principal principal){
        Document savedDoc = documentService.uploadDocument(projectId, principal.getName(), file);
        return ResponseEntity.ok(savedDoc);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments(@PathVariable Long projectId){
        return ResponseEntity.ok(documentService.getDocumentsByProjectId(projectId));
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable Long projectId, @PathVariable String fileName){
        InputStream stream = documentService.downloadDocument(fileName);
        InputStreamResource resource = new InputStreamResource(stream);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long projectId, @PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok("Document deleted successfully");
    }
}
