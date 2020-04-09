package musicprojectuploadservice.music_upload_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Random;

@Service
public class UploadImageService {
    private static final String IMAGE_PATH = "image/";
    private static final String USER_PATH = "users/";

    private final AmazonS3 s3client;

    @Value("${bucket}")
    private String bucketName;

    public UploadImageService(AmazonS3 s3client) {
        this.s3client = s3client;
    }


    public String uploadImageFile(String name, File file) {
        String filePath = generateString(IMAGE_PATH + name);

        while (fileExists(filePath)) {
            filePath = generateString(IMAGE_PATH + name);
        }

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(request);

        FileService.cleanup(file);
        return s3client.getUrl(bucketName, filePath).toString();
    }

    public String uploadUserImage(String name, File file) {
        String filePath = generateString(USER_PATH + name);

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(request);

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

}
