package com.example.cloudstorage.controllers;

import com.example.cloudstorage.dto.FileDataResponse;
import com.example.cloudstorage.dto.RenameFileName;
import com.example.cloudstorage.service.FileDataService;
import com.example.cloudstorage.util.LoggerMessages;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

@RestController
public class FileController {

    private final FileDataService service;

    public FileController(FileDataService service) {
        this.service = service;
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String fileName
            , @RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info(LoggerMessages.FILE_LOAD.getMessage(fileName));
        service.fileUpload(fileName, multipartFile);
        LOGGER.info(LoggerMessages.FILE_LOAD_SUCCESS.getMessage(fileName));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String fileName) {
        LOGGER.info(LoggerMessages.FILE_DELETE.getMessage(fileName));
        service.delete(fileName);
        LOGGER.info(LoggerMessages.FILE_DELETE_SUCCESS.getMessage(fileName));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam("filename") String fileName) {
        LOGGER.info(LoggerMessages.FILE_DOWNLOAD.getMessage(fileName));
        return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA).body(service.fileDownload(fileName));
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestParam("filename") String fileName, @RequestBody RenameFileName renameFileName) {
        LOGGER.info(LoggerMessages.EDIT_FILE_NAME.getMessage(fileName));
        service.editFileName(fileName, renameFileName);
        LOGGER.info(LoggerMessages.EDIT_FILE_NAME_SUCCESS.getMessage(fileName, renameFileName.getFileName()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public List<FileDataResponse> getList(@RequestParam int limit) {
        LOGGER.info(LoggerMessages.GET_FILES_LIST.getMessage());
        List<FileDataResponse> list = service.getList(limit);
        LOGGER.info(LoggerMessages.GET_FILES_LIST_SUCCESS.getMessage());
        return list;
    }
}
