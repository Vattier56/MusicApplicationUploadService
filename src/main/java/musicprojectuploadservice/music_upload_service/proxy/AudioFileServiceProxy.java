package musicprojectuploadservice.music_upload_service.proxy;

import musicprojectuploadservice.music_upload_service.model.FileMetaResponse;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.File;


@FeignClient(name = "audio-file-service")
@RibbonClient(name = "audio-file-service")
public interface AudioFileServiceProxy {

    @PostMapping(value="/file-service", consumes = "multipart/form-data" )
    FileMetaResponse fetchAudioFileMetadata(@RequestPart("file") File file);

}
