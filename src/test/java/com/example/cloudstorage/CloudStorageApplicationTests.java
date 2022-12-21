package com.example.cloudstorage;

import com.example.cloudstorage.dto.LoginRequest;
import com.example.cloudstorage.dto.RenameFileName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Paths;

@SpringBootTest
class CloudStorageApplicationTests {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private String uploadedFileName;
    private ResultActions resultActions;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mapper = new MappingJackson2HttpMessageConverter().getObjectMapper();
        resultActions = upload();
    }

    ResultActions upload() throws Exception {
        uploadedFileName = "file.xml";
        Resource resource = new UrlResource(Paths.get("./pom.xml").toUri());
        MockMultipartFile multipartFile = new MockMultipartFile("file"
                , resource.getFilename(), "text/hml", resource.getInputStream());
        return mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                        .file(multipartFile)
                        .param("filename", uploadedFileName));
    }

    @Test
    void login() throws Exception {
        String login = "evg@mail.ru";
        String password = "qwerty";
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new LoginRequest(login, password))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.auth-token").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void uploadFile() throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deleteFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/file")
                        .param("filename", uploadedFileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void downloadFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/file")
                        .param("filename", uploadedFileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void editFileName() throws Exception {
        String newFileName = "rename.xml";
        mockMvc.perform(MockMvcRequestBuilders.put("/file")
                        .param("filename", uploadedFileName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RenameFileName(newFileName))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        uploadedFileName = newFileName;
    }

    @Test
    void getList() throws Exception {
        int size = 3;
        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .param("limit", String.valueOf(size)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

}
