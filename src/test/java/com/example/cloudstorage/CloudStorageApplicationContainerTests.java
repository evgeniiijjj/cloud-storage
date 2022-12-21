package com.example.cloudstorage;

import com.example.cloudstorage.dto.FileDataResponse;
import com.example.cloudstorage.dto.JwtResponse;
import com.example.cloudstorage.dto.LoginRequest;
import com.example.cloudstorage.dto.RenameFileName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudStorageApplicationContainerTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Container
    private GenericContainer<?> container;

    private String uploadedFileName;
    private HttpHeaders headers;
    private ResponseEntity<JwtResponse> responseForLoginTest;
    private ResponseEntity<String> responseForUploadFileTest;

    private static final String host = "http://localhost:";

    @BeforeEach
    void setUp() throws Exception {
        container = new GenericContainer<>("cloud-storage-backend");
        container.addExposedPort(8000);
        container.start();
        responseForLoginTest = getAuthorizationToken();
        headers = new HttpHeaders();
        headers.add("auth-token", "Bearer "
                + Objects.requireNonNull(responseForLoginTest.getBody()).getToken());
        responseForUploadFileTest = upload();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    }

    ResponseEntity<JwtResponse> getAuthorizationToken() {
        String login = "evg@mail.ru";
        String password = "qwerty";
        return restTemplate.postForEntity(host
                        + container.getMappedPort(8000) + "/login", new LoginRequest(login, password)
                , JwtResponse.class);
    }

    ResponseEntity<String> upload() throws Exception {
        uploadedFileName = "file.xml";
        Resource resource = new UrlResource(Paths.get("./pom.xml").toUri());
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("filename", uploadedFileName);
        multiValueMap.add("file", resource);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return restTemplate.postForEntity(host + container.getMappedPort(8000) + "/file"
                , new HttpEntity<>(multiValueMap, headers), String.class);
    }

    @Test
    void login() {
        assertEquals(HttpStatus.OK, responseForLoginTest.getStatusCode());
    }

    @Test
    void uploadFile() {
        assertEquals(HttpStatus.OK, responseForUploadFileTest.getStatusCode());
    }

    @Test
    void deleteFile() {
        ResponseEntity<String> response = restTemplate.exchange(host + container.getMappedPort(8000) + "/file?filename=" + uploadedFileName
                , HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void downloadFile() {
        ResponseEntity<String> response = restTemplate.exchange(host + container.getMappedPort(8000) + "/file?filename=" + uploadedFileName
                , HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.MULTIPART_FORM_DATA.getType(), response.getHeaders().getContentType().getType());
    }

    @Test
    void editFileName() {
        String newFileName = "new_file_name.xml";
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(host + container.getMappedPort(8000) + "/file?filename=" + uploadedFileName
                , HttpMethod.PUT, new HttpEntity<>(new RenameFileName(newFileName), headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getList() {
        int limit = 3;
        ResponseEntity<List<FileDataResponse>> response = restTemplate.exchange(host + container.getMappedPort(8000) + "/list?limit=" + limit
                , HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {});
        System.out.println(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
}
