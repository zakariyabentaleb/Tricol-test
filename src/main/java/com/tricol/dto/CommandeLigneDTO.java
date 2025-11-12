package com.tricol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeLigneDTO {
    private int id;
    private int produitId;
    private int commandeId;
    private int quantite;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double prixAchat;
}