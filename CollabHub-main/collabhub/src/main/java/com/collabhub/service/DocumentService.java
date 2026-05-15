package com.collabhub.service;


import com.collabhub.controller.ResourceNotFoundException;
import com.collabhub.model.Document;
import com.collabhub.model.Project;
import com.collabhub.model.User;
import com.collabhub.repository.DocumentRepository;
import com.collabhub.repository.ProjectRepository;
import com.collabhub.repository.UserRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    private final MinioClient minioClient;
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public List<Document> getDocumentsByProjectId(Long projectId){
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return documentRepository.findByProject_Id(projectId);
    }

    public Document uploadDocument(Long projectId, String username, MultipartFile file){
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User uploader = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try{
            minioClient.putObject(PutObjectArgs.builder().
                    bucket(bucketName).
                    object(uniqueFileName).
                    stream(file.getInputStream(), file.getSize(), -1)
                    .build()
                    );
            log.info("File uploaded to MinIO: {}", uniqueFileName);

            Document document = new Document();
            document.setFilename(uniqueFileName);
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setProject(project);
            document.setUploader(uploader);

            return documentRepository.save(document);
        }catch (Exception e){
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Failed to upload document");
        }
    }

    public InputStream downloadDocument(String fileName){
        try{
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        }catch (Exception e){
            log.error("Error downloading file from MinIO: {}", fileName, e);
            throw new RuntimeException("Failed to download document");
        }
    }

    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(document.getFilename())
                            .build()
            );
            log.info("File deleted from MinIO: {}", document.getFilename());
        } catch (Exception e) {
            log.error("Error deleting file from MinIO: {}", document.getFilename(), e);
            throw new RuntimeException("Failed to delete document from storage");
        }

        documentRepository.delete(document);
        log.info("Document {} deleted from database", documentId);
    }
}
