package alim.com.imageApi.repository;

import alim.com.imageApi.entity.PublicImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicImageRepository extends JpaRepository<PublicImage,Long> {

}
