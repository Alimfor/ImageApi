package alim.com.imageApi.util;

import alim.com.imageApi.entity.Person;
import alim.com.imageApi.security.PersonDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticationUserUtil {
    public static Person getAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.person();
    }
}
