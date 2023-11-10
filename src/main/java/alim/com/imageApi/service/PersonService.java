package alim.com.imageApi.service;

import alim.com.imageApi.entity.Person;
import alim.com.imageApi.repository.PersonRepository;
import alim.com.imageApi.util.AuthenticationUserUtil;
import alim.com.imageApi.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findOne(final String email) {
        Optional<Person> person = personRepository.findByUserEmail(email);
        return person.orElse(new Person());
    }

    public boolean isThereSuchAnEmail(String email) {
        return personRepository.findByUserEmail(email).isPresent();
    }

    public boolean isTheOldPasswordCorrect(String oldPassword,String email) {
        Person person = findOne(email);

        if (person.getUserEmail() == null || person.getUserEmail().isEmpty())
            return false;

        return passwordEncoder.matches(oldPassword,person.getPassword());
    }

    public Person getInitializedAuthenticatedUser() {
        Person authenticatedUser = AuthenticationUserUtil.getAuthenticationUser();

        Person person = personRepository.findById(authenticatedUser.getPersonId())
                .orElse(null);

        if (person != null)
            Hibernate.initialize(person.getImages());

        return person;
    }
}
