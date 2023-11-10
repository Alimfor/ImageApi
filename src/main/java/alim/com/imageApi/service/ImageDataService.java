package alim.com.imageApi.service;

import alim.com.imageApi.entity.ImageData;
import alim.com.imageApi.entity.Person;
import alim.com.imageApi.entity.PublicImage;
import alim.com.imageApi.repository.PublicImageRepository;
import alim.com.imageApi.util.AuthenticationUserUtil;
import alim.com.imageApi.util.forUpdateEntity.UpdateImageNameUtil;
import alim.com.imageApi.util.ImageUploadResponseUtil;
import alim.com.imageApi.repository.ImageDataRepository;
import alim.com.imageApi.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageDataService {
    private final ImageDataRepository imageDataRepository;
    private final PublicImageRepository publicImageRepository;

    public List<byte[]> findAllPublic() {
        List<PublicImage> publicImages = publicImageRepository.findAll();

        return publicImages.stream()
                .map(publicImage ->
                        ImageUtil.decompressImage(
                                publicImage.getImageData().getImageData()
                        )
                )
                .toList();
    }

    @Transactional
    public ImageUploadResponseUtil uploadImage(final Person person, final MultipartFile file) throws IOException {
        final String userRole = person.getRole();

        ImageData imageData = ImageData.builder()
                .name(Objects.requireNonNull(file.getOriginalFilename()).toLowerCase())
                .type(file.getContentType())
                .imageData(ImageUtil.compressImage(file.getBytes()))
                .state("active")
                .access("private")
                .person(person).build();

        if (userRole.equals("ROLE_ADMIN")) {
            PublicImage publicImage = new PublicImage();
            publicImage.setImageData(imageData);
            publicImageRepository.save(publicImage);
        }

        postedTime(imageData);
        person.getImages().add(imageData);

        imageDataRepository.save(imageData);

        return new ImageUploadResponseUtil("Image upload successfully: " +
                file.getOriginalFilename());
    }

    public List<byte[]> findAllByPerson(Person person) {
        return imageDataRepository.findAllByPerson(person).stream()
                .map(image -> ImageUtil.decompressImage(image.getImageData()))
                .toList();
    }

    public ImageData getInfoByImageByName(final String name) {
        Optional<ImageData> dbImage = imageDataRepository.findByName(name);

        return ImageData.builder()
                .name(dbImage.get().getName())
                .type(dbImage.get().getType())
                .imageData(ImageUtil.decompressImage(dbImage.get().getImageData())).build();
    }


    public byte[] getImage(final String name) {
        Optional<ImageData> dbImage = imageDataRepository.findByName(name);

        return dbImage.map(imageData -> ImageUtil.decompressImage(
                imageData.getImageData())
        ).orElse(null);
    }

    @Transactional
    public void updateByName(final Person person,final UpdateImageNameUtil updateImageNameUtil) {
        Optional<ImageData> imageData = person.getImages().stream()
                .filter(image -> image.getName().equals(updateImageNameUtil.getOldName()))
                .findFirst();

        if (imageData.isEmpty()) return;

        imageData.get().setName(updateImageNameUtil.getNewName());
        imageDataRepository.save(imageData.get());
    }

    @Transactional
    public void deleteByName(final List<ImageData> images,final Person person,final String name) {
        ImageData imageData = images.stream()
                .filter(image -> image.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (imageData != null) {
            person.getImages().remove(imageData);
            imageDataRepository.delete(imageData);
        }
    }

    @Transactional
    public void changeImageStateByImageName(final Person person, final String imageName,
                                            final String IMAGE_STATE) {
        ImageData imageData = person.getImages().stream()
                .filter(image -> image.getName().equals(imageName))
                .findFirst().orElse(null);

        if (imageData == null) return;

        imageData.setState(IMAGE_STATE);
        imageDataRepository.save(imageData);
    }

    public void allowWaitingImage(final Long id,final String IMAGE_PUBLIC_ACCESS ,final String IMAGE_ALLOW_STATE) {
        Optional<ImageData> imageData = imageDataRepository.findById(id);

        if (imageData.isEmpty()) return;

        imageData.get()
                .setAccess(IMAGE_PUBLIC_ACCESS);
        imageData.get()
                .setState(IMAGE_ALLOW_STATE);

        imageDataRepository.save(imageData.get());

        PublicImage publicImage = new PublicImage();
        publicImage.setImageData(imageData.get());
        publicImageRepository.save(publicImage);
    }

    public void rejectWaitingImage(final Long id,final String IMAGE_REJECT_STATE) {
        Optional<ImageData> imageData = imageDataRepository.findById(id);

        if (imageData.isEmpty()) return;

        imageData.get()
                .setState(IMAGE_REJECT_STATE);

        imageDataRepository.save(imageData.get());
    }

    private void postedTime(final ImageData imageData) {
        imageData.setCreatedTime(LocalDateTime.now());
        updatedTime(imageData);
    }

    private void updatedTime(final ImageData imageData) {
        imageData.setUpdateTime(LocalDateTime.now());
    }
}
