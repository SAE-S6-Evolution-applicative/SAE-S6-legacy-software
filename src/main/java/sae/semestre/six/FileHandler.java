package sae.semestre.six;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileHandler is a utility class for handling file operations such as writing content to a file
 * and reading content from a file.
 */
@Component
public class FileHandler {

    private static final Logger log = LoggerFactory.getLogger(FileHandler.class);

    /**
     * Writes the given content to a file at the specified path.
     * If the parent directory does not exist, it will be created.
     *
     * @param contentToWrite the content to write to the file
     * @param pathName       the path where the file will be created
     */
    public void writeToFile(String contentToWrite, String pathName) {
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

    /**
     * Reads the content of a file at the specified path.
     *
     * @param path the path to the file
     * @return the content of the file as a String
     * @throws RuntimeException if the file cannot be read
     */
    public String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + path.toUri().getPath(), e);
        }
    }
}
