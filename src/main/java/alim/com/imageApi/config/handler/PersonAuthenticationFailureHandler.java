package alim.com.imageApi.config.handler;

import alim.com.imageApi.service.PersonDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class PersonAuthenticationFailureHandler implements AuthenticationFailureHandler {
   private final PersonDetailsService personDetailsService;
   private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String userEmail = request.getParameter("username");
        String password = request.getParameter("password");
        String status = personDetailsService.getPersonStatus(userEmail);

        if ("LOCKED".equals(status)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "your account has been locked by admin"
            );
        } else if (!personDetailsService.isEnabled(userEmail)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "your account has been deleted"
            );
        } else if (!personDetailsService.isCorrectPassword(userEmail, password, passwordEncoder)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "wrong password or email"
            );
        }
    }
}
