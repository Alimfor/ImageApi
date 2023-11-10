package alim.com.imageApi.config.filter;

import alim.com.imageApi.service.PersonDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class PersonDeletionCheckFilter extends OncePerRequestFilter {
    private final PersonDetailsService personDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            if (!personDetailsService.isEnabled(userDetails.getUsername())) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);

                log.warn("User: " + userDetails.getUsername() + " has been deleted!");

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "your account has been locked by admin"
                );
                return;
            }

            String userStatus = personDetailsService.getPersonStatus(userDetails.getUsername());
            if (userStatus.equals("LOCKED")) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);

                log.warn("User: " + userDetails.getUsername() + " has been locked!");

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Your account has been deleted by admin"
                );
            }
        }
    }
}
