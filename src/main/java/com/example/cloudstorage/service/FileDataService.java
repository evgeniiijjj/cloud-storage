package com.example.cloudstorage.service;

import com.example.cloudstorage.exceptions.InputDataErrorException;
import com.example.cloudstorage.exceptions.InternalServiceErrorException;
import com.example.cloudstorage.dto.ErrorResponse;
import com.example.cloudstorage.dto.FileDataResponse;
import com.example.cloudstorage.dto.RenameFileName;
import com.example.cloudstorage.entities.FileData;
import com.example.cloudstorage.repositories.FileDataRepository;
import com.example.cloudstorage.util.LoggerMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

@Service
public class FileDataService {

    private final FileDataRepository repository;

    @Value("${app.storage.path}")
    private String storagePath;

    public FileDataService(FileDataRepository repository) {
        this.repository = repository;
    }

    public void fileUpload(String fileName, MultipartFile multipartFile) {
        if (fileName == null || fileName.isEmpty() || multipartFile == null) {
            throw new InputDataErrorException(new ErrorResponse("Input data error", 1));
        }
        try {
            FileData file = repository.findByFileName(fileName).orElse(new FileData());
            file.setFileName(fileName);
            file.setContentType(multipartFile.getContentType());
            file.setSize(multipartFile.getSize());
            Path path = Paths.get(storagePath);
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
            Path directoryPath = Paths.get(storagePath + "/" + file.getContentType().replace("/", "-"));
            if (Files.notExists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
            multipartFile.transferTo(Paths.get(directoryPath + "/" + file.getFileName()));
            repository.save(file);
        } catch (Exception e) {
            throw new InternalServiceErrorException(new ErrorResponse("Internal service error", 1));
        }
    }

    public MultiValueMap<String, Object> fileDownload(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new InputDataErrorException(new ErrorResponse("Input data error", 2));
        }
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        try {
            FileData fileData = repository.findByFileName(fileName).orElseThrow();
            String contentType = fileData.getContentType();
            String fileFullName = storagePath + "/" + contentType.replace("/", "-")
                    + "/" + fileData.getFileName();
            Resource resource = new UrlResource(Paths.get(fileFullName).toUri());
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) resource.contentLength());
            resource.readableChannel().read(byteBuffer);
            formData.add("hash", DigestUtils.md5DigestAsHex(byteBuffer.array()));
            formData.add(fileName, byteBuffer.array());
        } catch (IOException e) {
            throw new InputDataErrorException(new ErrorResponse("Internal service error", 2));
        }
        LOGGER.info(LoggerMessages.FILE_DOWNLOAD_SUCCESS.getMessage(fileName));
        return formData;
    }

    public void delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new InputDataErrorException(new ErrorResponse("Input data error", 3));
        }
        try {
            repository.delete(repository.findByFileName(fileName).get());
        } catch (Exception e) {
            throw new InputDataErrorException(new ErrorResponse("Internal service error", 3));
        }
    }

    public void editFileName(String fileName, RenameFileName renameFileName) {
        if (fileName == null || fileName.isEmpty() || renameFileName == null
                || renameFileName.getFileName().isEmpty()) {
            throw new InputDataErrorException(new ErrorResponse("Input data error", 4));
        }
        try {
            FileData fileData = repository.findByFileName(fileName).get();
            String directoryName = storagePath + "/" + fileData.getContentType().replace("/", "-") + "/";
            if (new File(directoryName + fileName).renameTo(new File(directoryName + renameFileName.getFileName()))) {
                fileData.setFileName(renameFileName.getFileName());
                repository.save(fileData);
            }
        } catch (Exception e) {
            throw new InputDataErrorException(new ErrorResponse("Internal service error", 4));
        }
    }

    public List<FileDataResponse> getList(int limit) {
        if (limit == 0) {
            throw new InputDataErrorException(new ErrorResponse("Input data error", 5));
        }
        try {
            return repository.findAll(PageRequest.of(0, limit)).stream()
                    .map(fileData -> new FileDataResponse(fileData.getFileName()
                            , (int) fileData.getSize()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InputDataErrorException(new ErrorResponse("Internal service error", 5));
        }
    }
}
