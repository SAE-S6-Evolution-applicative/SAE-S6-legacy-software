package sae.semestre.six;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileHandler {

    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    public void writeHashToFile(String contentToWrite, String pathName) {
        File billHashFile = new File(pathName);

        File parentDir = billHashFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                log.error("Unable to create parent folder for : {}", pathName);
            }
        }

        try (FileWriter fileWriter = new FileWriter(billHashFile)) {
            fileWriter.write(contentToWrite);
        } catch (IOException e) {
            log.error("The file cannot be written");
            log.debug(e.getMessage(), e);
        }
    }

    public String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + path.toUri().getPath(), e);
        }
    }
}
