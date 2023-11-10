package alim.com.imageApi.controller;

import alim.com.imageApi.dto.PersonDTO;
import alim.com.imageApi.security.PersonDetails;
import alim.com.imageApi.service.PersonService;
import alim.com.imageApi.util.ResponseEntityUtil;
import alim.com.imageApi.util.mapper.PersonMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private static final String GET_USER_PERSONAL_DATA = "/personal-data";

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(GET_USER_PERSONAL_DATA)
    public ResponseEntity<?> getPersonalData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails userDetails = (PersonDetails) authentication.getPrincipal();

        PersonDTO personDTO = PersonMapperUtil.mapToPersonDTO(
                userDetails.person()
        );

        return ResponseEntityUtil.sendResponse(HttpStatus.OK, MediaType.APPLICATION_JSON,personDTO);
    }
}
