package alim.com.imageApi.controller;

import alim.com.imageApi.dto.AuthenticationDTO;
import alim.com.imageApi.dto.PersonDTO;
import alim.com.imageApi.security.JWTSecurity;
import alim.com.imageApi.service.RegistrationService;
import alim.com.imageApi.util.forUpdateEntity.UpdateForgottenPasswordUtil;
import alim.com.imageApi.util.mapper.PersonMapperUtil;
import alim.com.imageApi.util.validator.PersonValidatorUtil;
import alim.com.imageApi.util.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PersonValidatorUtil personValidatorUtil;
    private final RegistrationService registrationService;
    private final JWTSecurity jwtSecurity;
    private final AuthenticationManager authenticationManager;

    private static final String PATCH_CHANGE_FORGOTTEN_PASSWORD = "/change-password";
    private static final String POST_LOGIN = "/log-in";
    private static final String POST_REGISTER_PERSON = "/registration";
    private static final String POST_PERFORM_LOGIN = "/login";

    @PatchMapping(PATCH_CHANGE_FORGOTTEN_PASSWORD)
    public ResponseEntity<?> updateForgottenPassword(@RequestBody UpdateForgottenPasswordUtil updateForgottenPasswordUtil,
                                                     BindingResult bindingResult) {
        personValidatorUtil.validate(updateForgottenPasswordUtil, bindingResult);

        if (bindingResult.hasErrors())
            return ResponseEntityUtil.sendResponse(HttpStatus.BAD_REQUEST, MediaType.APPLICATION_JSON,
                    Map.of(bindingResult, updateForgottenPasswordUtil)
            );

        registrationService.updateForgottenPassword(updateForgottenPasswordUtil.getEmail(),
                updateForgottenPasswordUtil.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(POST_LOGIN)
    public ResponseEntity<String> authenticateUser(@RequestBody AuthenticationDTO authenticationDTO) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authenticationDTO.getUserEmail(),
                                authenticationDTO.getPassword()
                        )
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(POST_REGISTER_PERSON)
    public ResponseEntity<?> personRegistration(@RequestBody PersonDTO personDTO,
                                                BindingResult bindingResult) {
        personValidatorUtil.validate(personDTO, bindingResult);

        if (bindingResult.hasErrors())
            return ResponseEntityUtil.sendResponse(
                    HttpStatus.BAD_REQUEST, MediaType.APPLICATION_JSON,
                    Map.of(bindingResult, personDTO)
            );

        registrationService.register(PersonMapperUtil.mapToPerson(personDTO));

        String token = jwtSecurity.generateToken(personDTO.getUserEmail());
        return ResponseEntityUtil.sendResponse(
                HttpStatus.CREATED, MediaType.APPLICATION_JSON,
                Map.of("jwt-token", token)
        );
    }

    @GetMapping(POST_PERFORM_LOGIN)
    public ResponseEntity<?> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUserEmail(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException ex) {
            return ResponseEntityUtil.sendResponse(
                    HttpStatus.BAD_REQUEST, MediaType.APPLICATION_JSON,
                    Map.of("Message", "Incorrect credentials!")
            );
        }

        String token = jwtSecurity.generateToken(authenticationDTO.getUserEmail());
        return ResponseEntityUtil.sendResponse(
                HttpStatus.OK, MediaType.APPLICATION_JSON,
                Map.of("jwt-token", token)
        );
    }
}
