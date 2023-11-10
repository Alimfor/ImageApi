package alim.com.imageApi.util.forUpdateEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateForgottenPasswordUtil {
    private String email;
    private String oldPassword;
    private String newPassword;
}
