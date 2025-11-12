package com.tricol.mapper;


import com.tricol.dto.CommandeDTO;
import com.tricol.model.Commande;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommandeMapper {
    CommandeDTO  toDTO(Commande commande);
    Commande toEntity(CommandeDTO commandeDTO);
}

