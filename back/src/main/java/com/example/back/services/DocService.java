package com.example.back.services;

import com.example.back.entities.*;
import com.example.back.entities.Document;
import com.example.back.repository.DocumentRepository;
import com.example.back.repository.DocumentSpecifications;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.io.InputStream;

@Service
public class DocService {
    private final DocumentRepository documentRepo;
    private final MinioClient minioClient;

    public DocService(DocumentRepository documentRepo, MinioClient minioClient) {
        this.documentRepo = documentRepo;
        this.minioClient = minioClient;
    }

    @Value("${minio.bucketName}")
    private String bucketName;

    private void uploadFile(String objectKey, MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    private InputStream downloadFile(String objectKey) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()
        );
    }

    public String putDoc(DocPutRequest request) {
        if (request.title().isEmpty() || request.author().isEmpty() || request.description().isEmpty() || request.language().isEmpty() || request.type().isEmpty() || request.file().isEmpty() || request.year() == null) {
            return "invalid args";
        }

        try {
            String storageKey = UUID.randomUUID().toString();

            uploadFile(storageKey, request.file());

            Document document = new Document();
            document.setTitle(request.title());
            document.setAuthor(request.author());
            document.setYear(request.year());
            document.setDescription(request.description());
            document.setType(request.type());
            document.setLanguage(request.language());
            document.setStorageKey(storageKey);
            documentRepo.save(document);
            return "ok";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public DocGetServiceResponse getDocs(DocsGetRequest request) {
        try {
            Specification<Document> spec = DocumentSpecifications.withFilters(request);
            List<Document> documents = documentRepo.findAll(spec);
            return new DocGetServiceResponse(true, "ok", documents.stream()
                    .map(doc -> new DocGetResponse(
                            doc.getId(),
                            doc.getTitle(),
                            doc.getAuthor(),
                            doc.getYear(),
                            doc.getDescription(),
                            doc.getType(),
                            doc.getLanguage()
                    )).toList());
        } catch (Exception e) {
            return new DocGetServiceResponse(false, e.getMessage(), null);
        }
    }

    public DownloadResponse generateDownloadResponse(Long id) {
        try {
            Document doc = documentRepo.findById(id).orElse(null);
            if (doc == null) {
                return new DownloadResponse(false, "no such document", null, null);
            }
            InputStream stream = downloadFile(doc.getStorageKey());
            String fullFileName = doc.getTitle() + doc.getType();
            return new DownloadResponse(true, "ok", fullFileName, stream);
        } catch (Exception e) {
            return new DownloadResponse(false, "storage error: " + e.getMessage(), null, null);
        }
    }

    public String updateDoc(Long id, DocChangeRequest request) {
        Document doc = documentRepo.findById(id).orElse(null);
        if (doc == null) {
            return "no such document";
        }

        if (!request.title().isEmpty() && !request.title().isBlank()) {
            doc.setTitle(request.title());
        }

        if (!request.author().isEmpty() && !request.author().isBlank()) {
            doc.setAuthor(request.author());
        }

        if (!request.description().isEmpty() && !request.description().isBlank()) {
            doc.setDescription(request.description());
        }

        if (!request.type().isEmpty() && !request.type().isBlank()) {
            doc.setType(request.type());
        }

        if (!request.language().isEmpty() && !request.language().isBlank()) {
            doc.setLanguage(request.language());
        }

        if (request.year() !=  null) {
            doc.setYear(request.year());
        }

        try {
            documentRepo.save(doc);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String deleteDoc(Long id) {
        Document doc = documentRepo.findById(id).orElse(null);
        if (doc == null) {
            return "no such document";
        }

        try {
            documentRepo.delete(doc);
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
