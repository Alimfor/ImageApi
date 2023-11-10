package alim.com.imageApi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonDTO {
    private String userEmail;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String password;
}
