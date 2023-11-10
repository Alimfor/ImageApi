package alim.com.imageApi.util.mapper;

import alim.com.imageApi.dto.ImageDataDTO;
import alim.com.imageApi.entity.ImageData;
import org.modelmapper.ModelMapper;

public class ImageDataMapperUtil {
    private static final ModelMapper modelMapper = ModelMapperManager.getModelMapper();

    public static ImageData mapToImageData(ImageDataDTO imageDataDTO) {
        return modelMapper.map(imageDataDTO,ImageData.class);
    }

    public static ImageDataDTO mapToImageDataDTO(ImageData imageData) {
        return modelMapper.map(imageData,ImageDataDTO.class);
    }
}
