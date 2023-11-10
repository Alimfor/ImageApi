package alim.com.imageApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDTO {
    private String userEmail;
    private String password;
}
