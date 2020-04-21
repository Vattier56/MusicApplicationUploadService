package musicprojectuploadservice.music_upload_service.controller.rest;

import musicprojectuploadservice.music_upload_service.exception.FileNotFoundException;
import musicprojectuploadservice.music_upload_service.exception.FileProcessingException;
import musicprojectuploadservice.music_upload_service.exception.UploadServiceException;
import musicprojectuploadservice.music_upload_service.model.FileMetaResponse;
import musicprojectuploadservice.music_upload_service.proxy.AudioFileServiceProxy;
import musicprojectuploadservice.music_upload_service.service.FileService;
import musicprojectuploadservice.music_upload_service.service.ImageService;
import musicprojectuploadservice.music_upload_service.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final ImageService imageService;
    private final UploadService uploadService;
    private final AudioFileServiceProxy audioFileProxy;

    public UploadController(ImageService imageService, UploadService uploadService, AudioFileServiceProxy audioFileProxy) {
        this.imageService = imageService;
        this.uploadService = uploadService;
        this.audioFileProxy = audioFileProxy;
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

        try {
            imageService.uploadImages(FileService.convert(image));
        } catch (Exception e) {
            throw new UploadServiceException("Cannot upload image");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/audio")
    public ResponseEntity saveAudioFile(File file) {

        if(!file.isFile())
            throw new FileNotFoundException("File parameter not found");

        try {
            FileService.save(file);
        } catch (Exception e) {
            throw new FileProcessingException("Cannot save file");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/audio/info")
    public ResponseEntity saveAudioFileAndFetchMeta(File file) {

        if(!file.isFile())
            throw new FileNotFoundException("File parameter not found");

        FileMetaResponse response;
        try {
            response = audioFileProxy.fetchAudioFileMetadata(file);

            FileService.save(file);
        } catch (IOException e) {
            throw new FileProcessingException("Cannot save file");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/audio/save")
    public ResponseEntity uploadAudioFile(String filePath, int userHash) {

        String path = "";
       try {
           File file = FileService.findFileByPath(filePath);

           if(!file.exists())
               throw new FileNotFoundException("File not found");

           path = uploadService.uploadAudioFile(file, userHash);
       } catch (Exception e) {
           throw new UploadServiceException("Cannot upload file");
       }
        return ResponseEntity.ok(path);
    }

}
