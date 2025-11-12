package com.tricol.mapper;

import com.tricol.dto.MouvementStockDTO;
import com.tricol.model.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MouvementStockMapper {

    MouvementStockDTO toDTO(MouvementStock mouvementStock);
    MouvementStock toEntity(MouvementStockDTO mouvementStockDTO);
}
