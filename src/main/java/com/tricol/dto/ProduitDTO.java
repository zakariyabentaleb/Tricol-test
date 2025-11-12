package com.tricol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitDTO {
    private int id;
    private String nom;
    private String description;
    private double prixUnitaire;
    private String categorie;
    private int stockActuel;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double coutMoyenUnitaire;
}
