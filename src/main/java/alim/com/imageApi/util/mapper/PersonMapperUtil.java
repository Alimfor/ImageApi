package alim.com.imageApi.util.mapper;

import alim.com.imageApi.dto.AdminDTO;
import alim.com.imageApi.dto.PersonDTO;
import alim.com.imageApi.entity.Person;
import org.modelmapper.ModelMapper;

public class PersonMapperUtil {
    private static final ModelMapper modelMapper = ModelMapperManager.getModelMapper();

    public static Person mapToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO,Person.class);
    }

    public static PersonDTO mapToPersonDTO(Person person) {
        return modelMapper.map(person,PersonDTO.class);
    }

    public static AdminDTO mapToAdminDTO(Person person) {
        return modelMapper.map(person,AdminDTO.class);
    }
}
