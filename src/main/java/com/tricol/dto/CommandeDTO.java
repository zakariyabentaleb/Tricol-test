package com.tricol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tricol.enums.StatutCommande;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeDTO {
    private int id;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private Double montantTotal;
    private int fournisseurId;
}