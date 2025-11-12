package com.tricol.mapper;


import com.tricol.dto.CommandeDTO;
import com.tricol.model.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeMapper {
    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    CommandeDTO  toDTO(Commande commande);
    @Mapping(source = "fournisseurId", target = "fournisseur.id")
    Commande toEntity(CommandeDTO commandeDTO);
}

