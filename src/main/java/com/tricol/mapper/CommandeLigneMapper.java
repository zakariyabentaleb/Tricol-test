package com.tricol.mapper;

import com.tricol.dto.CommandeLigneDTO;
import com.tricol.model.CommandeLigne;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommandeLigneMapper {
    CommandeLigneDTO toDTO(CommandeLigne commandeLigne);
    CommandeLigne toEntiry(CommandeLigneDTO commandeLigneDTO);
}
