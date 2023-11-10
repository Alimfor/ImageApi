package alim.com.imageApi.service;

import alim.com.imageApi.entity.Person;
import alim.com.imageApi.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(final Person person) {
        person.setPassword(
                passwordEncoder.encode(person.getPassword())
        );

        person.setRole(
                personRepository.count() == 0
                        ? "ROLE_FIRST"
                        : "ROLE_USER"
        );

        person.setStatus("ACTIVE");
        registrationTime(person);
        personRepository.save(person);
    }

    public void updateForgottenPassword(final String email, final String newPassword) {
        Optional<Person> person = personRepository.findByUserEmail(email);

        if (person.isEmpty()) return;

        person.get().setPassword(
                passwordEncoder.encode(newPassword)
        );

        updatedTime(person.get());
        personRepository.save(person.get());
    }

    private void registrationTime(final Person person) {
        person.setCreatedWho("REGISTERED");
        person.setCreatedTime(LocalDateTime.now());
        updatedTime(person);
    }

    private void updatedTime(final Person person) {
        person.setUpdateTime(LocalDateTime.now());
    }
}
