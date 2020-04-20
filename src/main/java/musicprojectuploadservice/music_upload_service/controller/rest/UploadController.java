package musicprojectuploadservice.music_upload_service.controller.rest;

import musicprojectuploadservice.music_upload_service.exception.FileNotFoundException;
import musicprojectuploadservice.music_upload_service.exception.FileProcessingException;
import musicprojectuploadservice.music_upload_service.exception.UploadServiceException;
import musicprojectuploadservice.music_upload_service.service.FileService;
import musicprojectuploadservice.music_upload_service.service.ImageService;
import musicprojectuploadservice.music_upload_service.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;


@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    private final ImageService imageService;
    private final UploadService uploadService;

    public UploadController(ImageService imageService, UploadService uploadService) {
        this.imageService = imageService;
        this.uploadService = uploadService;
    }

    @PostMapping("/image/user")
    public ResponseEntity saveUserImage(@RequestParam(value = "image") MultipartFile image, @RequestParam(value = "oldImagePath") String oldImagePath, @RequestParam(value = "userNickName")String userNickName) {

        if(image.isEmpty())
            throw new FileNotFoundException("File parameter not found");

        File imageFile = FileService.convert(image);

        try {
            imageService.uploadUserImage(imageFile, oldImagePath, userNickName);
        } catch (Exception e) {
            throw new UploadServiceException("Cannot upload image");
        }
        return ResponseEntity.ok().build();
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

    @PostMapping("/music")
    public ResponseEntity saveAudioFile(File file) {

        if(!file.isFile())
            throw new FileNotFoundException("File parameter not found");

        try {
            FileService.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadServiceException("Cannot upload image");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/music/save")
    public ResponseEntity uploadAudioFile(String filePath, int userHash) {

        String path = "";
       try {
           File file = FileService.findFileByPath(filePath);

           if(!file.exists())
               throw new FileNotFoundException("File not found");

           path = uploadService.uploadAudioFile(file, userHash);
       } catch (Exception e) {
           throw new FileProcessingException("Unable to save file");
       }
        return ResponseEntity.ok(path);
    }

}
