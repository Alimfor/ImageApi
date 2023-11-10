package alim.com.imageApi.repository;

import alim.com.imageApi.entity.ImageData;
import alim.com.imageApi.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageDataRepository extends JpaRepository<ImageData,Long> {
    Optional<ImageData> findByName(String name);
    List<ImageData> findAllByPerson(Person person);
    List<ImageData> findAllByAccess(String access);
    void deleteByName(String name);
}
