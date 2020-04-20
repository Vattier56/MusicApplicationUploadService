package musicprojectuploadservice.music_upload_service.service;

import musicprojectuploadservice.music_upload_service.exception.FileProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileService {

    /*DEFAULT WORKING DIRECTORY PATH*/
    private static final String FILE_PATH = System.getProperty("user.dir") + "/ ";

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(FileService.class);

    public static File convert(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(convFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new FileProcessingException("File conversion error !");
        }
        return convFile;
    }

    public static File findFileByPath(String fileName) {
        return new File(FILE_PATH + fileName);
    }


    public static void save(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            Files.write(Paths.get(FILE_PATH + file.getName()), fileContent);
        } catch (Exception e){
            throw new FileProcessingException("Unable to save file");
        }
    }

    static void cleanup(File file) {
        boolean deleted = false;
        try {
            deleted = file.delete();
        } catch (Exception e) {
            logger.error("File cleanup error !");
        }
        if(deleted) {
            logger.info("Cleanup successful - file deleted" + file.getName());
        }
    }
}
