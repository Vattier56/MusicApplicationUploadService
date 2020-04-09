package musicprojectuploadservice.music_upload_service.controller.rest;

import musicprojectuploadservice.music_upload_service.exception.FileNotFoundException;
import musicprojectuploadservice.music_upload_service.exception.UploadServiceException;
import musicprojectuploadservice.music_upload_service.service.UploadImageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;


@RestController("/upload")
public class UploadController {

    private final UploadImageService imageService;

    public UploadController(UploadImageService imageService) {
        this.imageService = imageService;
    }


    @PostMapping("/image/user")
    public String saveUserImage(File image) {

        if(!image.isFile())
            throw new FileNotFoundException("File parameter not found");

        String imagePath = imageService.uploadUserImage(image.getName(), image);

        if(imagePath.isEmpty())
            throw new UploadServiceException("S3 upload exception");

        return imagePath;
    }

    @PostMapping("/image/music")
    public String saveSongImage(File image) {

        if(!image.isFile())
            throw new FileNotFoundException("File parameter not found");

        String imagePath = imageService.uploadImageFile(image.getName(), image);

        if(imagePath.isEmpty())
            throw new UploadServiceException("S3 upload exception");

        return imagePath;
    }

}
