package musicprojectuploadservice.music_upload_service.repository;

import musicprojectuploadservice.music_upload_service.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Transactional
    @Modifying
    @Query(value = "update user set user.image_id = :ImageID where user.nick_name = :UserNick", nativeQuery = true)
    void updateUserImage(@Param("ImageID") Long id, @Param("UserNick") String userNick);

    @Transactional
    @Modifying
    void deleteImageByImagePath(String imagePath);

}
