package alim.com.imageApi.service;

import alim.com.imageApi.entity.Person;
import alim.com.imageApi.repository.PersonRepository;
import alim.com.imageApi.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUserEmail(username);

        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new PersonDetails(person.get());
    }

    public String getPersonStatus(String email) {
        Optional<Person> person = personRepository.findByUserEmail(email);

        return person.isPresent()
                ? person.get().getStatus()
                : "User not found";
    }

    public boolean isEnabled(String email) {
        Optional<Person> person = personRepository.findByUserEmail(email);
        return person.isPresent();
    }

    public boolean isCorrectPassword(String email, String password, PasswordEncoder passwordEncoder) {
        Optional<Person> person = personRepository.findByUserEmail(email);
        return person.filter(value ->
            passwordEncoder.matches(password,value.getPassword())
        ).isPresent();
    }
}
