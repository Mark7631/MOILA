package com.example.back.controllers;

import com.example.back.entities.*;
import com.example.back.enums.UserRole;
import com.example.back.services.DocService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {
    private final DocService docService;

    public DocumentController(DocService docService) {
        this.docService = docService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> login(DocsGetRequest docsGetRequest,
                                   @SessionAttribute(name = "email", required = false) String email) {
        if (email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not allowed for unauthorized users"));
        }

        DocGetServiceResponse response = docService.getDocs(docsGetRequest);
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", response.message()));
        }
        return ResponseEntity.ok(response.docs());
    }

    @PostMapping("/put")
    public ResponseEntity<?> handleFileUpload(@ModelAttribute DocPutRequest request,
                                              @SessionAttribute(name = "role", required = false) UserRole role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not able for unauthorized users"));
        }

        if (role.equals(UserRole.USER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "not allowed for users"));
        }

        String res = docService.putDoc(request);

        if (res.equals("ok")) {
            return ResponseEntity.ok().body(Map.of("message", "successful"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", res));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") Long id,
                                          @SessionAttribute(name = "email", required = false) String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not allowed for unauthorized users"));
        }

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id is null"));
        }

        DownloadResponse response = docService.generateDownloadResponse(id);
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", response.message()));
        }

        InputStreamResource resource = new InputStreamResource(response.doc());
        String encodedFileName = URLEncoder.encode(response.fileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateDoc(@PathVariable Long id,
                                       @RequestBody DocChangeRequest request,
                                       @SessionAttribute(name = "role", required = false) UserRole role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not allowed for unauthorized users"));
        }

        if (role.equals(UserRole.USER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "no such rights"));
        }

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id is null"));
        }

        String res = docService.updateDoc(id, request);
        if (res.equals("ok")) {
            return ResponseEntity.ok(Map.of("message", "ok"));
        } else if (res.equals("no such document")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "no such document"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", res));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> updateDoc(@PathVariable Long id,
                                       @SessionAttribute(name = "role", required = false) UserRole role) {
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not allowed for unauthorized users"));
        }

        if (role.equals(UserRole.USER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "no such rights"));
        }

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id is null"));
        }

        String res = docService.deleteDoc(id);
        if (res.equals("ok")) {
            return ResponseEntity.ok(Map.of("message", "ok"));
        } else if (res.equals("no such document")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "no such document"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", res));
    }
}
