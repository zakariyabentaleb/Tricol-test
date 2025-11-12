package com.tricol.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(name = "prix_unitaire")
    private double prixUnitaire;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "stock_actuel")
    private int stockActuel;

    @Column(name = "cout_moyen_unitaire")
    private double coutMoyenUnitaire;

}
