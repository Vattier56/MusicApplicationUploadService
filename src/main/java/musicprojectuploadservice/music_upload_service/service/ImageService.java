package musicprojectuploadservice.music_upload_service.service;

import musicprojectuploadservice.music_upload_service.exception.UploadServiceException;
import musicprojectuploadservice.music_upload_service.model.Image;
import musicprojectuploadservice.music_upload_service.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class ImageService {

    @Value("${bucket}")
    private String bucketName;
    @Autowired
    private static Logger logger = LoggerFactory.getLogger(ImageService.class);

    private static final String DEFAULT_USER_IMAGE = "https://veloo.s3.eu-west-3.amazonaws.com/users/blank_user.png";


    private final UploadService uploadService;
    private final ImageRepository imageRepository;

    public ImageService(UploadService uploadService, ImageRepository imageRepository) {
        this.uploadService = uploadService;
        this.imageRepository = imageRepository;
    }


    public void uploadImages(File image) {
        String path = uploadService.uploadImageFile(image.getName(), image);

        if(path.isEmpty())
            throw new UploadServiceException("S3 upload exception");

        imageRepository.save(new Image(image.getName(), path));
        logger.info("Image {} saved", image);
    }

    public void uploadUserImage(File image, String oldImagePath, String userNickName) {
        String path = uploadService.uploadUserImage(image.getName(), image);
        Image newImage = new Image(image.getName(), path);
        imageRepository.save(newImage);

        imageRepository.updateUserImage(newImage.getId(), userNickName);

        if(!oldImagePath.equals(DEFAULT_USER_IMAGE))  {

            uploadService.deleteS3Object(oldImagePath);
            imageRepository.deleteImageByImagePath(oldImagePath);

        }
        logger.info("Image {} saved", newImage);
    }
}
