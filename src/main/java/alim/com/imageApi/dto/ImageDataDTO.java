package alim.com.imageApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDataDTO {
    private String name;
    private String type;
    private byte[] imageData;
}
