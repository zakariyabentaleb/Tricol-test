package com.tricol.mapper;

import com.tricol.dto.FournisseurDTO;
import com.tricol.model.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {

    FournisseurMapper INSTANCE = Mappers.getMapper(FournisseurMapper.class);

    FournisseurDTO toDTO(Fournisseur fournisseur);

    Fournisseur toEntity(FournisseurDTO fournisseurDTO);
}
