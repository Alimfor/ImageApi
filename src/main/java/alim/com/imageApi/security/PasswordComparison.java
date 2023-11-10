package alim.com.imageApi.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PasswordComparison {
    @Value("${expected.password}")
    private String expectedPassword;

    @Setter
    private String actualPassword;

    public boolean isActualPasswordValidate() {
        return expectedPassword.equals(actualPassword);
    }
}
