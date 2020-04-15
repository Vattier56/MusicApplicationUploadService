package musicprojectuploadservice.music_upload_service.repository;


import musicprojectuploadservice.music_upload_service.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
