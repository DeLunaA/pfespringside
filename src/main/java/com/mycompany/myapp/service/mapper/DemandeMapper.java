package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Demande;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.DemandeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Demande} and its DTO {@link DemandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandeMapper extends EntityMapper<DemandeDTO, Demande> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    DemandeDTO toDto(Demande s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
