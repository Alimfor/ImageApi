package alim.com.imageApi.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
public class PersonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        boolean hasUserRole = false;
        boolean hasAdminRole = false;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (var grantedAuthorities : authorities) {
            String personRole = grantedAuthorities.getAuthority();
            if (personRole.equals("ROLE_USER") || personRole.equals("ROLE_FIRST")) {
                hasUserRole = true;
                break;
            } else if (personRole.equals("ROLE-ADMIN")) {
                hasAdminRole = true;
                break;
            }
        }

        if (hasAdminRole) {
            log.info("Admin: " + request.getParameter("username") + " is came at " + LocalDateTime.now());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else if (hasUserRole)
            redirectStrategy.sendRedirect(request,response,"/api/image/all/private");
        else {
            log.error("Something is happen in PersonAuthenticationSuccessHandler class!\n" +
                    new IllegalStateException().getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
