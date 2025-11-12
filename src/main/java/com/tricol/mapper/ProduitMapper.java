package com.tricol.mapper;



import com.tricol.model.Produit;
import com.tricol.dto.ProduitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitMapper INSTANCE = Mappers.getMapper(ProduitMapper.class);

    ProduitDTO toDTO(Produit produit);

    Produit toEntity(ProduitDTO produitDTO);
}
