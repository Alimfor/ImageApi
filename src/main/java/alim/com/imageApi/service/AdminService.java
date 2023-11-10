package alim.com.imageApi.service;

import alim.com.imageApi.entity.Person;
import alim.com.imageApi.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_FIRST')")
@RequiredArgsConstructor
public class AdminService {
    private final PersonRepository personRepository;

    public void switchRole(Person person, String newRole) {
        person.setRole(newRole);
        personRepository.save(person);
    }

    public void alterStatus(Long id, String personStatus) {
        Optional<Person> person = personRepository.findById(id);

        if (person.isEmpty()) return;

        person.get().setStatus(personStatus);
    }
}
