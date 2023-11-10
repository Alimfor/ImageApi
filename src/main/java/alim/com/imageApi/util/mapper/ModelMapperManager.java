package alim.com.imageApi.util.mapper;

import lombok.Getter;
import org.modelmapper.ModelMapper;

public class ModelMapperManager {
    @Getter
    private static final ModelMapper modelMapper = new ModelMapper();
}
