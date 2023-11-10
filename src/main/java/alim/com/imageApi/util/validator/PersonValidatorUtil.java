package alim.com.imageApi.util.validator;


import alim.com.imageApi.dto.PersonDTO;
import alim.com.imageApi.service.PersonService;
import alim.com.imageApi.util.forUpdateEntity.UpdateForgottenPasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PersonValidatorUtil implements Validator {
    private final PersonService personService;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@(?:gmail|mail)\\.(?:com|ru)$";

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof UpdateForgottenPasswordUtil) {
            validateUpdateForgottenPassword((UpdateForgottenPasswordUtil) target, errors);
        } else if (target instanceof PersonDTO) {
            validatePersonDTO((PersonDTO) target, errors);
        }
    }

    private void validateUpdateForgottenPassword(UpdateForgottenPasswordUtil updatePassword, Errors errors) {
        if (updatePassword.getEmail() == null || !updatePassword.getEmail().matches(EMAIL_REGEX)) {
            errors.rejectValue("userEmail", "userEmail.invalid", "Invalid email format");
        }

        if (!personService.isThereSuchAnEmail(updatePassword.getEmail())) {
            errors.rejectValue("email","email.wrong","That email is not exists");
        }

        if (updatePassword.getOldPassword() == null || updatePassword.getOldPassword().isEmpty()) {
            errors.rejectValue("oldPassword", "oldPassword.empty", "Old password is required");
        }

        if (!personService.isTheOldPasswordCorrect(updatePassword.getOldPassword(), updatePassword.getEmail())) {
            errors.rejectValue("oldPassword", "oldPassword.wrong", "Old password is wrong");
        }

        if (updatePassword.getNewPassword() == null || updatePassword.getNewPassword().isEmpty()) {
            errors.rejectValue("newPassword", "newPassword.empty", "New password is required");
        }
    }

    private void validatePersonDTO(PersonDTO personDTO, Errors errors) {
        if (personDTO.getUserEmail() == null || !personDTO.getUserEmail().matches(EMAIL_REGEX)) {
            errors.rejectValue("userEmail", "userEmail.invalid", "Invalid email format");
        }

        if (personDTO.getFirstName() == null || personDTO.getFirstName().isEmpty() || personDTO.getLastName().length() > 20) {
            errors.rejectValue("firstName", "firstName.empty", "First name must be exceed 0 character and not exceed 20 characters");
        }

        if (personDTO.getLastName() == null || personDTO.getLastName().isEmpty() || personDTO.getLastName().length() > 20) {
            errors.rejectValue("lastName", "lastName.empty", "Last name name must be exceed 0 character and not exceed 20 characters");
        }

        if (personDTO.getPatronymic() == null || personDTO.getLastName().isEmpty() || personDTO.getPatronymic().length() > 20) {
            errors.rejectValue("patronymic", "patronymic.tooLong", "Patronymic name must be exceed 0 character and not exceed 20 characters");
        }

        if (personDTO.getPassword() == null || personDTO.getPassword().isEmpty()) {
            errors.rejectValue("password", "password.empty", "Password is required");
        } else if (personDTO.getPassword().length() < 8 || personDTO.getPassword().length() > 100) {
            errors.rejectValue("password", "password.length", "Password must be between 8 and 100 characters");
        }
    }
}
