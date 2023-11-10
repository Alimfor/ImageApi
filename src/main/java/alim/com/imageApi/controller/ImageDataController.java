package alim.com.imageApi.controller;

import alim.com.imageApi.entity.ImageData;
import alim.com.imageApi.entity.Person;
import alim.com.imageApi.service.PersonService;
import alim.com.imageApi.util.ImageUtil;
import alim.com.imageApi.util.forUpdateEntity.UpdateImageNameUtil;
import alim.com.imageApi.util.ImageUploadResponseUtil;
import alim.com.imageApi.service.ImageDataService;
import alim.com.imageApi.util.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageDataController {
    private final ImageDataService imageDataService;
    private final PersonService personService;

    private static final String GET_ALL_PUBLIC_IMAGES_ROLE_NONE = "/all/public";
    private static final String GET_ALL_USER_IMAGES_ROLE_AUTHENTICATED = "/all/private";
    private static final String GET_IMAGE_INFO_BY_NAME_ROLE_AUTHENTICATED = "/info/{name}";
    private static final String PATCH_RENAME_IMAGE_ROLE_AUTHENTICATED = "/rename";
    private static final String DELETE_IMAGE_BY_NAME_ROLE_AUTHENTICATED = "/delete/{deleting-name}";
    private static final String POST_ADD_NEW_IMAGE_ROLE_AUTHENTICATED = "/new";
    private static final String PATCH_VERIFY_WAITING_IMAGE_ROLE_ADMIN = "/allow_waiting_image/{image_id}";
    private static final String PATCH_DENY_WAITING_IMAGE_ROLE_ADMIN = "/reject_waiting_image/{image_id}";
    private static final String PATCH_SEND_FOR_VERIFY_ROLE_USER = "/to_public/{image_name}";

    @PreAuthorize("isAnonymous()")
    @GetMapping(GET_ALL_PUBLIC_IMAGES_ROLE_NONE)
    public ResponseEntity<?> sendAllPublicImages() {
        List<byte[]> publicImages = imageDataService.findAllPublic();

        return ResponseEntityUtil.sendResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, publicImages);
    }

    @GetMapping(GET_ALL_USER_IMAGES_ROLE_AUTHENTICATED)
    public ResponseEntity<?> sendAllPrivateImages() {
        Person person = personService.getInitializedAuthenticatedUser();
        List<byte[]> privateImages = person.getImages().stream()
                    .map(image -> ImageUtil.decompressImage(image.getImageData()))
                    .toList();

        return ResponseEntityUtil.sendResponse(HttpStatus.OK, MediaType.APPLICATION_JSON , privateImages);
    }

    @GetMapping(GET_IMAGE_INFO_BY_NAME_ROLE_AUTHENTICATED)
    public ResponseEntity<?> getImageInfoByName(@PathVariable("name") String name) {
        Person person = personService.getInitializedAuthenticatedUser();

        ImageData image = person.getImages().stream()
                .filter(imageData -> imageData.getName().equals(name))
                .findFirst().orElse(null);

        byte[] imageBytes = ImageUtil.decompressImage(Objects.requireNonNull(image).getImageData());
        return ResponseEntityUtil.sendResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, imageBytes);
    }

    @PatchMapping(PATCH_RENAME_IMAGE_ROLE_AUTHENTICATED)
    public ResponseEntity<?> renameImage(@RequestBody UpdateImageNameUtil updateImageNameUtil) {
        Person person = personService.getInitializedAuthenticatedUser();

        imageDataService.updateByName(person, updateImageNameUtil);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(DELETE_IMAGE_BY_NAME_ROLE_AUTHENTICATED)
    public ResponseEntity<?> deleteImageByName(@PathVariable("deleting-name") String name) {
        Person person = personService.getInitializedAuthenticatedUser();

        imageDataService.deleteByName(person.getImages(), person, name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(POST_ADD_NEW_IMAGE_ROLE_AUTHENTICATED)
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        Person person = personService.getInitializedAuthenticatedUser();

        try {
            ImageUploadResponseUtil response = imageDataService.uploadImage(person, file);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{name}").buildAndExpand(
                            Objects.requireNonNull(file.getOriginalFilename()).toLowerCase()
                    )
                    .toUri();

            return ResponseEntity.created(location)
                    .body(response);
        } catch (IOException ioException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(PATCH_VERIFY_WAITING_IMAGE_ROLE_ADMIN)
    public ResponseEntity<?> verifyImage(@PathVariable("image_id") Long id) {
        final String IMAGE_ALLOW_STATE = "allow";
        final String IMAGE_PUBLIC_ACCESS = "public";
        imageDataService.allowWaitingImage(id, IMAGE_PUBLIC_ACCESS, IMAGE_ALLOW_STATE);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(PATCH_DENY_WAITING_IMAGE_ROLE_ADMIN)
    public ResponseEntity<?> rejectImage(@PathVariable("image_id") Long id) {
        final String IMAGE_REJECT_STATE = "rejected";
        imageDataService.rejectWaitingImage(id, IMAGE_REJECT_STATE);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PatchMapping(PATCH_SEND_FOR_VERIFY_ROLE_USER)
    public ResponseEntity<?> sendImageToVerify(@PathVariable("image_name") String imageName) {
        Person person = personService.getInitializedAuthenticatedUser();
        final String IMAGE_WAITING_STATE = "waiting";

        imageDataService.changeImageStateByImageName(person, imageName, IMAGE_WAITING_STATE);
        return ResponseEntity.noContent().build();
    }
}
