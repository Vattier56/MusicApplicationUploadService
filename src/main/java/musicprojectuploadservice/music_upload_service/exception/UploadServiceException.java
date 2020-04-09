package musicprojectuploadservice.music_upload_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadServiceException extends RuntimeException {

    public UploadServiceException(String message) {
        super(message);
    }
}
