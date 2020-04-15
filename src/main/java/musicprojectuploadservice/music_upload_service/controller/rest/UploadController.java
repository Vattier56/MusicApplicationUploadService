package musicprojectuploadservice.music_upload_service.controller.rest;

import musicprojectuploadservice.music_upload_service.exception.FileNotFoundException;
import musicprojectuploadservice.music_upload_service.exception.UploadServiceException;
import musicprojectuploadservice.music_upload_service.service.FileService;
import musicprojectuploadservice.music_upload_service.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;


@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    private final ImageService imageService;

    public UploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/image/music")
    public ResponseEntity saveSongImage(@RequestParam(value = "image") MultipartFile image) {

        if(image.isEmpty())
            throw new FileNotFoundException("File parameter not found");

        File imageFile = FileService.convert(image);
        try {
            imageService.uploadImages(imageFile);
        } catch (Exception e) {
            throw new UploadServiceException("Cannot upload image");
        }
        return ResponseEntity.ok().build();
    }

}
