package musicprojectuploadservice.music_upload_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Random;
import java.util.concurrent.Executors;

@Service
public class UploadService {
    /*DEFAULT ALBUM COVER IMAGE PATH DIRECTORY IN S3 */
    private static final String IMAGE_PATH = "image/";
    /*DEFAULT USER IMAGE PATH DIRECTORY IN S3 */
    private static final String USER_PATH = "users/";
    /*DEFAULT MUSIC PATH DIRECTORY IN S3 */
    private static final String MUSIC_FILES_PATH = "music/";

    private final AmazonS3 s3client;

    @Value("${bucket}")
    private String bucketName;
    @Autowired
    private static Logger logger = LoggerFactory.getLogger(UploadService.class);

    public UploadService(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadAudioFile(File file, int userHash) throws InterruptedException {
        String filePath = MUSIC_FILES_PATH + userHash + "/" + file.getName();

        if(fileExists(filePath)) filePath = addFileVersions(filePath);

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file).withCannedAcl(CannedAccessControlList.PublicRead);
        TransferManager transferManager = TransferManagerBuilder
                .standard()
                .withS3Client(s3client)
                .withMultipartUploadThreshold((long) (5 * 1024 * 1024))
                .withExecutorFactory(() -> Executors.newFixedThreadPool(5))
                .build();

        Upload upload = transferManager.upload(request);

        upload.waitForCompletion();
        FileService.cleanup(file);
        return transferManager.getAmazonS3Client().getUrl(bucketName, filePath).toString();
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

    private String addFileVersions(String fileName) {
        try {
            if(fileName.contains("(")) {
                String number = fileName.substring(fileName.lastIndexOf("(") + 1, fileName.lastIndexOf(")"));

                int versionNumber = Integer.parseInt(number);
                versionNumber++;

                fileName = fileName.replaceAll(number, String.valueOf(versionNumber));
            } else {
                String []newPath = fileName.split("\\.");
                newPath[0] += "(1).";

                fileName = newPath[0] + newPath[1];

            }
        } catch (Exception e) {
            String []parts = fileName.split("\\.");

            parts[parts.length - 2] += "(1)";

            for (int i = 0; i < parts.length - 1; i++) {
                parts[i] += ".";
            }
            return String.join("", parts);
        }
        return fileName;
    }

}
