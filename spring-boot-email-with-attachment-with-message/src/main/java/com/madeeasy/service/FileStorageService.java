package com.madeeasy.service;

import com.madeeasy.model.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Objects;

@Service
public class FileStorageService {

    public Files storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new FileSystemException("Sorry! Filename contains invalid path sequenth " + fileName);
            }

            Files dbFile = new Files(fileName, file.getContentType(), file.getBytes());
            return dbFile;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Files storeListOfFiles(List<MultipartFile> list) {
        Files storedFile = null;
        for (MultipartFile file : list) {
            storedFile = storeFile(file);
        }
        return storedFile;
    }
}
