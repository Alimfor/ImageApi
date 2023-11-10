package alim.com.imageApi.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class PersonAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Anonymous has attempted to access the protected URL: " + request.getRequestURI());
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Anonymous does not has any permission to enter there"
            );
            return;
        }

        log.warn("User: " + " attempted to access the protected URL: " +
                authentication.getName() + request.getRequestURI());

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "User: " + authentication.getName() + " does not has any permission to enter there"
        );
    }
}
