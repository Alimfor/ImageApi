package alim.com.imageApi.controller;

import alim.com.imageApi.dto.AdminDTO;
import alim.com.imageApi.entity.Person;
import alim.com.imageApi.security.PasswordComparison;
import alim.com.imageApi.service.AdminService;
import alim.com.imageApi.service.PersonService;
import alim.com.imageApi.util.AuthenticationUserUtil;
import alim.com.imageApi.util.PersonStatusRequestUtil;
import alim.com.imageApi.util.ResponseEntityUtil;
import alim.com.imageApi.util.mapper.PersonMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@PreAuthorize("hasRole('ROlE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final PersonService personService;
    private final PasswordComparison passwordComparison;
    private final AdminService adminService;

    private static final String GET_ALL_USER_LIST = "/user-list";
    private static final String POST_VALIDATE_PASSWORD = "/validate";
    private static final String PATCH_SWITCH_ROLE = "/switch_to";
    private static final String PATCH_LOCK_USER = "/lock";
    private static final String PATCH_ACTIVATE_USER = "/activate";

    @GetMapping(GET_ALL_USER_LIST)
    public ResponseEntity<?> getAllUser() {
        List<AdminDTO> people = personService.findAll()
                .stream().map(PersonMapperUtil::mapToAdminDTO)
                .toList();

        return ResponseEntityUtil.sendResponse(HttpStatus.OK, MediaType.APPLICATION_JSON,people);
    }

    @PostMapping(POST_VALIDATE_PASSWORD)
    public ResponseEntity<?> checkingPassword(@RequestBody PasswordComparison actualPassword) {
        passwordComparison.setActualPassword(actualPassword.getActualPassword());

        return ResponseEntityUtil.sendResponse(
                HttpStatus.OK,
                MediaType.APPLICATION_JSON,
                Map.of("Is valid",passwordComparison.isActualPasswordValidate())
        );
    }

    @PatchMapping(PATCH_SWITCH_ROLE)
    public ResponseEntity<?> updateRole(@RequestParam String newRole,
                             @RequestParam("personEmail") String email) {
        if (!email.isEmpty()) {
            adminService.switchRole(personService.findOne(email),newRole);
            return ResponseEntity.noContent().build();
        }

        Person person = AuthenticationUserUtil.getAuthenticationUser();
        adminService.switchRole(person,newRole);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(PATCH_LOCK_USER)
    public ResponseEntity<?> lockUser(@RequestBody PersonStatusRequestUtil statusRequest) {
        adminService.alterStatus(statusRequest.getPersonId(), statusRequest.getPersonStatus());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(PATCH_ACTIVATE_USER)
    public ResponseEntity<?> activateUser(@RequestBody PersonStatusRequestUtil statusRequest) {
        adminService.alterStatus(statusRequest.getPersonId(), statusRequest.getPersonStatus());
        return ResponseEntity.noContent().build();
    }
}
