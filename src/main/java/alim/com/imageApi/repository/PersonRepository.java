package alim.com.imageApi.repository;

import alim.com.imageApi.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends  JpaRepository<Person,Long> {
    Optional<Person> findByUserEmail(String email);
}
