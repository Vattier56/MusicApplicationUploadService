package musicprojectuploadservice.music_upload_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileService {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(FileService.class);


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
