package com.tricol.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FournisseurDTO {

    private int id;

    @NotBlank(message = "La société est obligatoire")
    private String societe;
    private String adresse;
    private String contact;
    private String email;
    private String telephone;
    private String ville;
    private String ICE;
}
