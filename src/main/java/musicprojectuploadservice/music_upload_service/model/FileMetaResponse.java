package musicprojectuploadservice.music_upload_service.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileMetaResponse {

    private String title;
    private String author;
    private String album;
    private String genre;
    private String duration;
    private final String source;

    public FileMetaResponse(String title, String author, String album, String genre, String duration, String source) {
        this.title = title;
        this.author = author;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.source = source;
    }
}
