package musicprojectuploadservice.music_upload_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Random;

@Service
public class UploadService {
    private static final String IMAGE_PATH = "image/";
    private static final String USER_PATH = "users/";

    private final AmazonS3 s3client;

    @Value("${bucket}")
    private String bucketName;
    @Autowired
    private static Logger logger = LoggerFactory.getLogger(UploadService.class);

    public UploadService(AmazonS3 s3client) {
        this.s3client = s3client;
    }


    String uploadImageFile(String name, File file) {
        String filePath = IMAGE_PATH + generateString(name);

        while (fileExists(filePath)) {
            filePath = IMAGE_PATH + generateString(name);
        }

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(request);
        logger.info("File upload successful : {} ", file);

        FileService.cleanup(file);
        return s3client.getUrl(bucketName, filePath).toString();
    }

    String uploadUserImage(String name, File file) {
        String filePath = USER_PATH + generateString(name);

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(request);
        logger.info("File upload successful : {} ", file);

        FileService.cleanup(file);
        return s3client.getUrl(bucketName, filePath).toString();
    }


    private boolean fileExists(String filePath) {
        try {
            s3client.getObjectMetadata(bucketName, filePath);
        } catch(AmazonServiceException e) {
            return false;
        }
        return true;
    }

    private static String generateString(String name) {
        name = name.substring(0, name.length() -4);

        Random rng = new Random();
        int length = name.length();
        char[] text = new char[length];

        for (int i = 0; i < length; i++) {
            text[i] = name.charAt(rng.nextInt(name.length()));
        }

        return new String(text);
    }

    void deleteS3Object(String oldImage) {
        try {
            String[] oldPath = oldImage.split("/");
            s3client.deleteObject(bucketName, oldPath[3] + "/" + oldPath[4]);
        } catch (Exception e) {
            logger.error("Cannot delete file : {}", oldImage);
        }
    }

}
