package com.example.cloudstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SpringBootApplication
public class CloudStorageApplication {

    public final static Logger LOGGER = Logger.getLogger(CloudStorageApplication.class.getName());

    public static void main(String[] args) throws IOException {
        FileHandler fh = new FileHandler("logging/file.log", true);
        LOGGER.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        LOGGER.setUseParentHandlers(false);
        SpringApplication.run(CloudStorageApplication.class, args);
    }

}
